package myJs

import com.karasiq.bootstrap.Bootstrap.default._
import myJs.Utils._
import myJs.myPkg._
import org.querki.jquery._
import org.singlespaced.d3js.Ops._
import org.singlespaced.d3js.d3
import shared.Implicits._

import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}


/**
  * Created by yz on 2019/4/15
  */
@JSExportTopLevel("Predict")
object Predict {

  @JSExport("init")
  def init = {
//    showResult("322")

  }


  @JSExport("myRun")
  def myRun = {
    val bv = jQuery("#form").data("bootstrapValidator")
    bv.validate()
    val valid = bv.isValid().asInstanceOf[Boolean]
    if (valid) {
      val data = $("#form").serialize()
      val url = g.jsRoutes.controllers.PredictController.predict().url.toString
      val index = layer.alert(element, layerOptions)
      val ajaxSettings = JQueryAjaxSettings.url(url).
        `type`("post").data(data).success { (data, status, e) =>
        layer.close(index)
        val rs = data.asInstanceOf[js.Dictionary[js.Any]]
        if (rs.myGet("valid") == "false") {
          Swal.swal(SwalOptions.title("错误").text(rs.myGet("message")).`type`("error"))
          $("#result").hide()
        } else {
          val missionId=rs.myGet("missionId")
          PredictResult.showResult(missionId)
          g.reduceTime()

        }
      }
      $.ajax(ajaxSettings)
    }

  }

}
