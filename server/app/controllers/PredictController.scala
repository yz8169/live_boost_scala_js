package controllers

import java.io.File

import javax.inject.Inject
import org.apache.commons.io.FileUtils
import play.api.mvc.{AbstractController, ControllerComponents}
import tool._
import utils.Utils
import models.Tables._
import org.joda.time.DateTime
import dao._
import io.github.cloudify.scala.spdf.{Pdf, PdfConfig, Portrait}
import org.apache.commons.lang3.StringUtils
import play.api.libs.json.{JsObject, Json}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import utils.Implicits._
import shared._


/**
  * Created by yz on 2019/1/3
  */
class PredictController @Inject()(cc: ControllerComponents, formTool: FormTool, tool: Tool,
                                  missionDao: MissionDao, extraDataDao: ExtraDataDao,
                                  trashDao: TrashDao) extends AbstractController(cc) {

  def predictBefore = Action { implicit request =>
    Ok(views.html.user.predict())
  }

  def predict = Action.async { implicit request =>
    val data = formTool.predictForm.bindFromRequest().get
    val userId = tool.getUserId
    val tmpDir = tool.createTempDirectory("tmpDir")
    val inputFile = new File(tmpDir, "input.txt")
    val lines = ArrayBuffer(ArrayBuffer("SampleID", "Age", "AST", "ALT", "PLT"))
    lines += ArrayBuffer(data.sampleId, data.age, data.ast, data.alt, data.plt)
    Utils.lines2File(inputFile, lines.map(_.mkString("\t")))
    Utils.txt2Xlsx(inputFile, new File(tmpDir, "input.xlsx"))
    FileUtils.copyFileToDirectory(new File(Utils.dataDir, "LiveBoost_load.RData"), tmpDir)
    val linuxCommand =
      s"""
         |dos2unix *
         |Rscript ${Utils.dosPath2Dos(Utils.rPath)}/predict_v1.R
       """.stripMargin
    val windowsCommand =
      s"""
         |Rscript ${Utils.dosPath2Dos(Utils.rPath)}/predict_v1.R
       """.stripMargin
    val command = if (Utils.isWindows) windowsCommand else linuxCommand
    val startTime = System.currentTimeMillis()
    val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(command))
    if (execCommand.isSuccess) {
      val outFile = new File(tmpDir, "out.txt")
      val resultData = tool.getResultData(outFile)
      val score = Shared.calculateScore(resultData)
      val result = Shared.getResult(score)
      val time = new DateTime()
      val row = MissionRow(0, userId, data.sampleId, data.name, data.age, data.ast, data.alt, data.plt,
        score.toString(), result, time)
      missionDao.insertAndRetunId(row).flatMap { missionId =>
        val extraRow = tool.getEmptyExtraDataRow(missionId, userId).copy(sampleId = data.sampleId, name = data.name,
          checkDate = Utils.dataTime2String(time))
        val trashRow = TrashRow(missionId, userId, true)
        extraDataDao.insertOrUpdate(extraRow).zip(trashDao.insert(trashRow)).map { x =>
          val missionIdDir = tool.getMissionIdDirById(missionId)
          val outFile = new File(tmpDir, "out.txt")
          FileUtils.copyFileToDirectory(outFile, missionIdDir)
          tool.deleteDirectory(tmpDir)
          val json = Json.obj("missionId" -> missionId)
          Ok(json)
        }
      }
    } else {
      tool.deleteDirectory(tmpDir)
      Utils.result2Future(Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr)))
    }
  }

  def export = Action.async { implicit request =>
    val data = formTool.basicInfoForm.bindFromRequest().get
    val pdfData = formTool.pdfDataForm.bindFromRequest().get
    val missionId = formTool.missionIdForm.bindFromRequest().get.missionId
    val userId = tool.getUserId
    val tmpDir = tool.createTempDirectory("tmpDir")
    val outPdfFile = new File(tmpDir, "live_boost_out.pdf")
    val pdf = Pdf(new PdfConfig {
      orientation := Portrait
      pageSize := "A4"
      pageSize := "Letter"
      marginTop := "0.5in"
      marginBottom := "0in"
      marginLeft := "0.5in"
      marginRight := "0.5in"
    })
    missionDao.selectByMissionId(missionId).flatMap { row =>
      val missionIdDir = tool.getMissionIdDirById(missionId)
      val outFile = new File(missionIdDir, "out.txt")
      val pngFile = new File(missionIdDir, "out.png")
      val svgFile = new File(missionIdDir, "out.svg")
      FileUtils.writeStringToFile(svgFile, pdfData.svgStr)
      Utils.svg2png(svgFile)
      val base64 = Utils.getBase64Str(pngFile)
      val logoFile = new File(Utils.dataDir, "logo1.png")
      val logoBase64 = logoFile.base64
      val qrBase64 = new File(Utils.dataDir, "qr_code.jpg").base64
      val imageInfo = Map("barPlot" -> base64, "logo" -> logoBase64, "qrCode" -> qrBase64)
      val htmlFile = new File(tmpDir, "out.html")
      val str = views.html.user.html(data, row, imageInfo, pdfData).toString()
      FileUtils.writeStringToFile(htmlFile, str)
      pdf.run(views.html.user.html(data, row, imageInfo, pdfData).toString(), outPdfFile)
      val bytes = FileUtils.readFileToByteArray(outPdfFile)
      val extraDataRow = ExtraDataRow(row.id, row.userId, data.sample, data.unit, data.address, data.name, data.sex,
        data.office, data.doctor, data.number, data.sampleTime, data.submitTime, data.sampleType
        , data.sampleStatus, data.title, data.danger, data.reporter, data.checker, data.checkDate, data.reportDate
      )
      extraDataDao.insertOrUpdate(extraDataRow).map { x =>
        println("in")
        tool.deleteDirectory(tmpDir)
        Ok(bytes).as("application/pdf")
      }
    }

  }

  def batchPredictBefore = Action { implicit request =>
    Ok(views.html.user.batchPredict())
  }

  def downloadExampleFile = Action { implicit request =>
    val data = formTool.fileNameForm.bindFromRequest().get
    val file = new File(Utils.path, s"example/${data.fileName}")
    Ok.sendFile(file).withHeaders(
      CACHE_CONTROL -> "max-age=3600",
      CONTENT_DISPOSITION -> s"attachment; filename=${
        file.getName
      }",
      CONTENT_TYPE -> "application/x-download"
    )
  }

  def fileCheck = Action(parse.multipartFormData) { implicit request =>
    val tmpDir = tool.createTempDirectory("tmpDir")
    val dataFile = new File(tmpDir, "data.xlsx")
    val file = request.body.file("file").get
    file.ref.moveTo(dataFile, replace = true)
    val myMessage = tool.fileCheck(dataFile)
    tool.deleteDirectory(tmpDir)
    Ok(Json.obj("valid" -> myMessage.valid, "message" -> myMessage.message))
  }

  def batchPredict = Action.async(parse.multipartFormData) { implicit request =>
    val tmpDir = tool.createTempDirectory("tmpDir")
    val inputFile = new File(tmpDir, "input.xlsx")
    val file = request.body.file("file").get
    file.ref.moveTo(inputFile, replace = true)
    val userId = tool.getUserId
    val missionIds = ArrayBuffer[Int]()
    val time = new DateTime()
    FileUtils.copyFileToDirectory(new File(Utils.dataDir, "LiveBoost_load.RData"), tmpDir)
    val command =
      s"""
         |Rscript ${(Utils.rPath)}/predict_v1.R
       """.stripMargin
    val execCommand = Utils.callScript(tmpDir, shBuffer = ArrayBuffer(command))
    if (execCommand.isSuccess) {
      val outFile = new File(tmpDir, "out.txt")
      val outLines = Utils.file2Lines(outFile)

      val fileContents = outLines.drop(1).map { line =>
        ArrayBuffer(outLines.head, line)
      }
      val missions = fileContents.map { newLines =>
        val resultData = tool.getResultData(newLines)
        val score = Shared.calculateScore(resultData)
        val result = Shared.getResult(score)
        val orinalData = tool.getOrinalData(newLines)
        MissionRow(0, userId, orinalData.sampleId, "", orinalData.age, orinalData.ast, orinalData.alt, orinalData.plt,
          score.toString(), result, time)
      }
      val f = missionDao.insertAndRetunIds(missions).flatMap { missionIds =>
        val etRows = missionIds.zip(fileContents).map { case (missionId, newLines) =>
          val missionIdDir = tool.getMissionIdDirById(missionId)
          val outFile = new File(missionIdDir, "out.txt")
          Utils.lines2File(outFile, newLines)
          val extraData = tool.getExtraData(outFile)
          val checkDate = if (StringUtils.isEmpty(extraData.checkDate)) Utils.dataTime2String(time) else extraData.checkDate
          val orinalData = tool.getOrinalData(newLines)
          val extraRow = tool.getEmptyExtraDataRow(missionId, userId).copy(sampleId = orinalData.sampleId, name = extraData.name,
            unit = extraData.unit, address = extraData.address, sex = extraData.sex, office = extraData.office,
            doctor = extraData.doctor, number = extraData.number, sampleTime = extraData.sampleTime, submitTime = extraData.submitTime,
            sampleType = extraData.sampleType, sampleStatus = extraData.sampleStatus,
            title = extraData.title, reporter = extraData.reporter, checker = extraData.checker, checkDate = checkDate,
            reportDate = extraData.reportDate
          )
          val trashRow = TrashRow(missionId, userId, true)
          (extraRow, trashRow)
        }
        extraDataDao.insertOrUpdates(etRows).map { x =>
          missionIds
        }
      }
      val missionIds = Utils.execFuture(f)
      tool.deleteDirectory(tmpDir)
      missionDao.selectByMissionIds(missionIds).zip(extraDataDao.selectByMissionIds(missionIds)).map { case (x, extraRows) =>
        val missionRows = x
        val extraMap = extraRows.map(x => (x.id, x)).toMap
        val t2s = missionRows.map(x => (x, extraMap.getOrElse(x.id, tool.getEmptyExtraDataRow(x.id, x.userId))))
        val array = Utils.getArrayByT2s(t2s)
        Ok(Json.toJson(array))
      }
    } else {
      tool.deleteDirectory(tmpDir)
      Utils.result2Future(Ok(Json.obj("valid" -> "false", "message" -> execCommand.getErrStr)))
    }

  }

  def predictResult = Action.async { implicit request =>
    val data = formTool.missionIdForm.bindFromRequest().get
    missionDao.selectByMissionId(data.missionId).zip(extraDataDao.selectByMissionId(data.missionId)).map { case (row, extraRow) =>
      val missionIdDir = tool.getMissionIdDirById(data.missionId)
      val outFile = new File(missionIdDir, "out.txt")
      val resultData = tool.getResultData(outFile)
      val extraDataJson = Utils.getJsonByT(extraRow)
      val missionJson = tool.getMissionJson(row, resultData)
      val resultJson = Utils.getJsonByT(resultData)
      val json = Json.obj("missionId" -> data.missionId, "sampleId" -> row.sampleId,
        "name" -> row.name, "age" -> row.age, "extraData" -> extraDataJson, "mission" -> missionJson) ++ resultJson.as[JsObject]
      Ok(json)
    }

  }


}
