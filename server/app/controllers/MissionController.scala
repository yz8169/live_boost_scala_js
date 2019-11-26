package controllers

import dao._
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, ControllerComponents}
import tool.{FormTool, Tool}
import utils.Utils

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2019/1/3
  */
class MissionController @Inject()(cc: ControllerComponents, formTool: FormTool, missionDao: MissionDao,
                                  tool: Tool, extraDataDao: ExtraDataDao, trashDao: TrashDao) extends AbstractController(cc) {

  def missionManageBefore = Action { implicit request =>
    Ok(views.html.user.missionManage())
  }

  def deleteMissionByIds = Action.async {
    implicit request =>
      val data = formTool.missionIdsForm.bindFromRequest().get
      println(data)
      trashDao.deleteByIds(data.missionIds).map {
        x =>
         Ok("success!")
      }
  }

  def getAllMission = Action.async { implicit request =>
    val userId = tool.getUserId
    trashDao.selectAll(userId).flatMap { missionIds =>
      missionDao.selectByMissionIds(missionIds).zip(extraDataDao.selectByMissionIds(missionIds))
    }.map { case (missionRows, extraRows) =>
      val extraMap = extraRows.map(x => (x.id, x)).toMap
      val t2s = missionRows.map(x => (x, extraMap.getOrElse(x.id, tool.getEmptyExtraDataRow(x.id, x.userId))))
      val array = Utils.getArrayByT2s(t2s)
      Ok(Json.toJson(array))
    }
  }

  def deleteMissionById = Action.async {
    implicit request =>
      val data = formTool.missionIdForm.bindFromRequest().get
      trashDao.deleteById(data.missionId).map {
        x =>
          Ok("success!")
      }
  }


}
