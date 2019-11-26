package myJs

import myJs.Utils._
import myJs.myPkg._
import myJs.myPkg.Implicits._
import org.querki.jquery._

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSON
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.scalajs.js.JSConverters._


/**
  * Created by yz on 2019/4/15
  */
@JSExportTopLevel("MissionManage")
object MissionManage {

  val ids = ArrayBuffer[String]()

  @JSExport("init")
  def init = {
    $("#missionTable").bootstrapTable();
    updateMission{()=>
      bindEvt
    }


  }

  @JSExport("deleteMission")
  def deleteMission(id: String) = {
    val options = SwalOptions.title("").text("确定要删除此记录吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消").showLoaderOnConfirm(true)
    Swal.swal(options, () => {
      val url = g.jsRoutes.controllers.MissionController.deleteMissionById().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}?missionId=${id}").
        `type`("get").contentType("application/json").success { (data, status, e) =>
        updateMission { () =>
          Swal.swal(SwalOptions.title("成功").text("删除成功").`type`("success"))
        }
      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("移除失败").`type`("error"))
      }
      $.ajax(ajaxSettings)

    })
  }

  @JSExport("updateMission")
  def updateMission(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.MissionController.getAllMission().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      $("#missionTable").bootstrapTable("load", data)
      f()
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("deleteAllInfos")
  def deleteAllInfos = {
    val options = SwalOptions.title("").text("确定要删除选中的记录吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消").showLoaderOnConfirm(true)
    Swal.swal(options, () => {
      val url = g.jsRoutes.controllers.MissionController.deleteMissionByIds().url.toString
      val data = js.Dictionary(
        "missionIds" -> ids.toJSArray
      )
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}").data(JSON.stringify(data)).
        `type`("post").contentType("application/json").success { (data, status, e) =>
        updateMission { () =>
          Swal.swal(SwalOptions.title("成功").text("删除记录成功!").`type`("success"))
          getIds
        }

      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("删除失败!").`type`("error"))
      }
      $.ajax(ajaxSettings)

    })
  }

  def getIds = {
    ids.clear()
    val arrays = $("#missionTable").bootstrapTable("getSelections").asInstanceOf[js.Array[js.Dictionary[String]]]
    ids ++= arrays.map { x =>
      x("id")
    }
    if (ids.isEmpty) $("#deleteButton").attr("disabled", true) else
      $("#deleteButton").attr("disabled", false)

  }

  def bindEvt = {
    $("#missionTable").on("check.bs.table", () => getIds)
    $("#missionTable").on("uncheck.bs.table", () => getIds)
    $("#missionTable").on("check-all.bs.table", () => getIds)
    $("#missionTable").on("uncheck-all.bs.table", () => getIds)
    $("#missionTable").on("page-change.bs.table", () => getIds)
  }


}
