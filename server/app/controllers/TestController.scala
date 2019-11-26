package controllers

import javax.inject.Inject
import play.api.mvc.{AbstractController, ControllerComponents}
import models.Tables._
import dao._
import scala.concurrent.ExecutionContext.Implicits.global


/**
  * Created by yz on 2019/1/16
  */
class TestController @Inject()(cc: ControllerComponents, trashDao: TrashDao, missionDao: MissionDao,
                               userDao: UserDao, pdfInfoDao: PdfInfoDao) extends AbstractController(cc) {

  def refreshTrash = Action.async {
    missionDao.selectAll.zip(trashDao.selectAll).flatMap { case (x, trashs) =>
      val rows = x.map(y => TrashRow(y.id, y.userId, true)).filterNot(y => trashs.map(_.id).contains(y.id))
      trashDao.insertAll(rows)
    }.map { x =>
      Ok("success!")
    }
  }

  def refreshPdfInfo = Action.async { implicit request =>
    userDao.selectAll.zip(pdfInfoDao.selectAll).flatMap { case (users, pdfInfos) =>
      val rows = users.map(user => PdfInfoRow(user.id, "深圳绘云医学检验实验室", "", "", "", "")).
        filterNot(pdfInfo => pdfInfos.map(_.userId).contains(pdfInfo.userId))
      pdfInfoDao.insertAll(rows)
    }.map { x =>
      Ok("success!")
    }
  }

  def test = Action { implicit request =>
    println("a")
      Ok(views.html.test())
  }

}
