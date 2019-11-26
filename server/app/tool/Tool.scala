package tool

import java.io.File
import java.nio.file.Files

import dao.ModeDao
import javax.inject.Inject
import models.Tables.UserRow
import org.apache.commons.lang3.StringUtils
import org.joda.time.DateTime
import play.api.mvc.RequestHeader
import utils.{DesEncrypter, Utils}

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import models.Tables._
import play.api.libs.json.Json
import shared.ResultData

import scala.math.BigDecimal.RoundingMode
import shared._

/**
  * Created by yz on 2018/8/23
  */
class Tool @Inject()(modeDao: ModeDao) {

  def getUserName(implicit request: RequestHeader) = {
    request.session.get(Shared.userStr).get
  }

  def getTimes(times: Option[Int]) = times.map(_.toString).getOrElse("不限")

  def dataTime2String(dateTime: Option[DateTime]) = dateTime.map(_.toString("yyyy-MM-dd HH:mm:ss")).getOrElse("不限")

  def isb(user: UserRow) = user.remainTimes.map(_ > 0).getOrElse(true) && user.startTime.map(_.isBeforeNow).getOrElse(true) &&
    user.endTime.map(_.isAfterNow).getOrElse(true)

  val newPasswordDesEncrypter = new DesEncrypter("newPassword12345")

  val registerDesEncrypter = new DesEncrypter("register12345678")

  def getUserId(implicit request: RequestHeader) = {
    request.session.get(Utils.idStr).get.toInt
  }

  def createTempDirectory(prefix: String) = {
    if (isTestMode) Utils.testDir else Files.createTempDirectory(prefix).toFile
  }

  def isTestMode = {
    val mode = Utils.execFuture(modeDao.select)
    if (mode.test == "t") true else false
  }

  def deleteDirectory(direcotry: File) = {
    if (!isTestMode) Utils.deleteDirectory(direcotry)
  }

  def getPredictValue(file: File): Map[String, String] = {
    val lines = Utils.file2Lines(file)
    getPredictValue(lines)
  }

  def getPredictValue(lines: mutable.Buffer[String]): Map[String, String] = {
    val headers = lines.head.split("\t")
    headers.zip(lines(1).split("\t")).toMap
  }

  def getMissionIdDirById(missionId: Int)(implicit request: RequestHeader) = {
    val userMissionFile = getUserMissionDir
    new File(userMissionFile, missionId.toString)
  }

  def getUserMissionDir(implicit request: RequestHeader) = {
    val userIdDir = getUserIdDir
    new File(userIdDir, "mission")
  }

  def getUserIdDir(userId: Int) = {
    new File(Utils.userDir, userId.toString)
  }

  def getUserIdDir(implicit request: RequestHeader) = {
    val userId = getUserId
    new File(Utils.userDir, userId.toString)
  }

  def getResultData(file: File) = {
    val map = getPredictValue(file)
    ResultData(BigDecimal(map("Case")),BigDecimal(map("Cirrhosis")), BigDecimal(map("Late_Fibrosis")))
  }

  def getMissionJson(row: MissionRow, resultData: ResultData) = {
    val missionMap = Utils.getMapByT(row)
    Json.toJson(missionMap)
  }

  case class MyMessage(valid: Boolean, message: String)

  def fileCheck(file: File): MyMessage = {
    val lines = Utils.xlsx2Lines(file)
    val headers =lines.head.split("\t")
    if (StringUtils.isBlank(lines.head)) {
      return MyMessage(false, s"首行数据（表头）缺失!")
    }
    val repeatHeaders = headers.diff(headers.distinct)
    if (repeatHeaders.nonEmpty) {
      return MyMessage(false, s"文件表头 ${repeatHeaders.head} 重复!")
    }
    val hasHeaders = ArrayBuffer("Age", "AST", "ALT", "PLT", "SampleID")
    val noExistHeaders = hasHeaders.diff(headers)
    if (noExistHeaders.nonEmpty) {
      return MyMessage(false, s"文件表头 ${noExistHeaders.head} 不存在!")
    }
    lines.drop(1).zipWithIndex.foreach { case (line, i) =>
      val columns = line.split("\t")
      val lineMap = headers.zip(columns).toMap
      if (columns.size > headers.size) {
        return MyMessage(false, s"文件第${i + 2}行列数不正确,存在多余列!")
      }
      columns.zipWithIndex.foreach { case (tmpColumn, j) =>
        val column = tmpColumn.toLowerCase()
        val header = headers(j)
        if (StringUtils.isEmpty(column)) {
          return MyMessage(false, s"文件第${i + 2}行第${j + 1}列为空!")
        }
        val doubleColumns = ArrayBuffer("Age", "AST", "ALT", "PLT")
        if (doubleColumns.contains(header)) {
          if (!Utils.isDouble(column)) {
            return MyMessage(false, s"文件第${i + 2}行第${j + 1}列必须为实数!")
          }
        }
      }
    }
    MyMessage(true, "")
  }

  def getOrinalData(lines: mutable.Buffer[String]) = {
    val map = getPredictValue(lines)
    OrignalData(map("SampleID"), map("Age"), map("AST"), map("ALT"),
      map("PLT"))
  }

  def getResultData(lines: mutable.Buffer[String]) = {
    val map = getPredictValue(lines)
    ResultData(BigDecimal(map("Case")),BigDecimal(map("Cirrhosis")), BigDecimal(map("Late_Fibrosis")))
  }

  def getExtraData(file: File) = {
    val map = getPredictValue(file)
    ExtraDataF(getValue(map, "送检单位"), getValue(map, "地址"), getValue(map, "姓名"), getValue(map, "性别"),
      getValue(map, "送检科室"), getValue(map, "申请医生"), getValue(map, "门诊/住院号"), getValue(map, "采样时间"),
      getValue(map, "送检时间"), getValue(map, "样本类型"), getValue(map, "样本状态"),
      getValue(map, "标题"), getValue(map, "检验"), getValue(map, "审核"), getValue(map, "检验日期"),
      getValue(map, "报告日期")
    )
  }

  def getValue(map: Map[String, String], header: String) = {
    val str = map.getOrElse(header, "NA")
    if (str == "NA") "" else str
  }


  def getEmptyExtraDataRow(id: Int, userId: Int) = {
    ExtraDataRow(id, userId, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "")
  }


}
