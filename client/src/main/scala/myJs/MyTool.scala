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
@JSExportTopLevel("MyTool")
object MyTool {

  var score: BigDecimal = _
  var result: String = _

  @JSExport("init")
  def init = {

  }

  @JSExport("dealDanger")
  def dealDanger(extraDataJson: js.Dictionary[js.Any]) = {
    if (extraDataJson.myGet("danger") == "") {
      val danger = getDanger(score)
      $("#exportForm textarea[name='danger']").`val`(danger)
    } else {
      $("#exportForm textarea[name='danger']").`val`(extraDataJson.myGet("danger"))
    }

    $(":input[name='score']").`val`(score.toString())
    $(":input[name='result']").`val`(result)
    $(":input[name='svgStr']").`val`($("#svg1").html())
  }

  def getDanger(score: BigDecimal) = {
    score match {
      case x if x <= 0.25 => "肝脏代谢指标综合分析良好，提示比较健康，患有慢性肝病的可能性比较低。肝纤维化、肝硬化的可能性比较低。本结果仅供参考。"
      case x if x > 0.25 && x <= 0.5 => "肝脏代谢指标综合分析出现异常，提示患有慢性肝病的可能性非常大。早期肝纤维化的可能性非常大。本结果仅供参考，肝纤维化、肝硬化程度请结合临床。"
      case x if x > 0.5 && x <= 0.75 => "肝脏代谢指标综合分析出现异常，提示患有慢性肝病的可能性非常大。晚期肝纤维化的可能性非常大。本结果仅供参考，肝纤维化、肝硬化程度请结合临床。"
      case x if x > 0.75 => "肝脏代谢指标综合分析出现异常，提示患有慢性肝病的可能性非常大。肝硬化的可能性非常大。本结果仅供参考，肝纤维化、肝硬化程度请结合临床。"
    }

  }

}
