package myJs.admin

import myJs.Utils._
import myJs.myPkg.Implicits._
import myJs.myPkg._
import org.querki.jquery._

import scala.collection.mutable.ArrayBuffer
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.{Date, JSON}
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import com.karasiq.bootstrap.Bootstrap.default._

/**
  * Created by yz on 2019/4/15
  */
@JSExportTopLevel("LimitManage")
object LimitManage {


  @JSExport("init")
  def init = {
    $("#table").bootstrapTable()
    refreshUser()

  }

  @JSExport("deleteData")
  def deleteData(id: String) = {
    val options = SwalOptions.title("").text("确定要删除此数据吗？").`type`("warning").showCancelButton(true).
      showConfirmButton(true).confirmButtonClass("btn-danger").confirmButtonText("确定").closeOnConfirm(false).
      cancelButtonText("取消").showLoaderOnConfirm(true)
    Swal.swal(options, () => {
      val url = g.jsRoutes.controllers.AdminController.deleteUserById().url.toString
      val ajaxSettings = JQueryAjaxSettings.url(s"${url}?id=${id}").
        `type`("get").contentType("application/json").success { (data, status, e) =>
        refreshUser { () =>
          Swal.swal(SwalOptions.title("成功").text("删除成功").`type`("success"))
        }
      }.error { (data, status, e) =>
        Swal.swal(SwalOptions.title("错误").text("删除失败").`type`("error"))
      }
      $.ajax(ajaxSettings)

    })
  }

  @JSExport("refreshUser")
  def refreshUser(f: () => js.Any = () => ()) = {
    val url = g.jsRoutes.controllers.AdminController.getAllUser().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?").contentType("application/json").
      `type`("get").success { (data, status, e) =>
      $("#table").bootstrapTable("load", data)
      f()
    }
    $.ajax(ajaxSettings)

  }

  @JSExport("update")
  def update = {
    val formId = "updateForm"
    val bv = jQuery(s"#${formId}").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $(s"#${formId}").serialize()
      val url = g.jsRoutes.controllers.AdminController.updateUser().url.toString
      val index = layer.alert(element, layerOptions)
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        layer.close(index)
        jQuery("#updateModal").modal("hide")
        bv.resetForm(true)
        refreshUser { () =>
          Swal.swal(SwalOptions.title("成功").text("密码重置成功！").`type`("success"))
        }

      }
      $.ajax(ajaxSettings)
    }

  }

  @JSExport("addDays")
  def addDays = {
    val formId = "addDayForm"
    val bv = jQuery(s"#${formId}").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid && totalCheck) {
      val data = $(s"#${formId}").serialize()
      val url = g.jsRoutes.controllers.AdminController.addDays().url.toString
      val index = layer.alert(element, layerOptions)
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        layer.close(index)
        jQuery("#addDaysModal").modal("hide")
        bv.resetForm(true)
        refreshUser { () =>
          Swal.swal(SwalOptions.title("成功").text("设置权限成功！").`type`("success"))
        }

      }
      $.ajax(ajaxSettings)
    }

  }

  def timeCheck(timeId: String, msg: String) = {
    val end = $("#" + timeId + "endTime").`val`().toString
    val start = $("#" + timeId + "startTime").`val`().toString
    val startTime = new Date(start.replace("-", "/").replace("-", "/"))
    val endTime = new Date(end.replace("-", "/").replace("-", "/"))
    val startTimeDouble = startTime.getTime()
    val endTimeDouble = endTime.getTime()
    if (!startTimeDouble.isNaN && !endTimeDouble.isNaN &&
      startTimeDouble > endTimeDouble) {
      layer.msg(msg, LayerOptions.icon(5).time(1500))
      false
    } else true

  }

  def totalCheck = {
    timeCheck("", "开始日期不能大于截止日期！")
  }

}
