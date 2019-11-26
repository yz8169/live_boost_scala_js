package myJs

import org.singlespaced.d3js.Ops._
import org.singlespaced.d3js.d3
import org.querki.jquery._
import com.karasiq.bootstrap.Bootstrap.default._


import scala.scalajs.js
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import Utils._
import shared.Implicits._
import myPkg._


/**
  * Created by yz on 2019/4/15
  */
@JSExportTopLevel("PredictResult")
object PredictResult {

  @JSExport("init")
  def init = {
//    showResult("368")

  }

  @JSExport("showResult")
  def showResult(id: String) = {
    $("#result").hide()
    $("#mode2,#mode3").hide()
    val index = layer.load(1, layerOptions.shade(js.Array("0.1", "#fff")))
    val url = g.jsRoutes.controllers.PredictController.predictResult().url.toString
    val ajaxSettings = JQueryAjaxSettings.url(s"${url}?missionId=${id}").async(false).success { (data, status, e) =>
      val rs = data.asInstanceOf[js.Dictionary[js.Any]]
      layer.close(index)
      $("#showSampleId").text(rs.myGet("sampleId"))
      $("#showName").text(rs.myGet("name"))
      g.missionJson = rs("mission")
      g.extraDataJson = rs("extraData")
      dealPredictInfo(rs)
      $("#result").show()
      var target_top = $("#result").offset().top
      jQuery("html,body").animate(js.Dictionary("scrollTop" -> target_top), JQueryAnimationSettings.duration(800))
    }
    $.ajax(ajaxSettings)

  }

  def dealPredictInfo(rs: js.Dictionary[js.Any]) = {
    val missionDict = rs("mission").asInstanceOf[js.Dictionary[js.Any]]
    val score=missionDict.myGetDouble("score")
    $("#score").text(score.toString())
    refreshSvg(score, 1, "svg")
    refreshSvg(score, 2, "svg1")
    val result = missionDict.myGet("result")
    val pstr = s"计算得到的得分值为：${score},具有${result}。"
    $("#suggestion1").text(pstr)
    MyTool.score = score
    MyTool.result = result

  }

  def refreshSvg(score: BigDecimal, scale: Double, svgId: String) = {
    $(s"#${svgId}").empty()
    val width = 750 * scale
    val height = 160 * scale

    val totalWidth = 720
    val markerWidth = 10
    val scoreWidth = score * totalWidth - markerWidth
    val lw = "0.5"
    val colors = js.Array("#03FF00", "#D6FF00", "#FFEC00", "#FF9B00", "#FF1A00", "#C20000")
    val offsets = js.Array(0, 0.3, 0.4, 0.6, 0.85, 1)
    val tickts = offsets.drop(1).dropRight(1)
    val svg = d3.select(s"#${svgId}").append("svg").attr("width", width).
      attr("height", height).attr("xmlns", "http://www.w3.org/2000/svg").
      attr("xmlns:xlink", "http://www.w3.org/1999/xlink")
    val lengendSvg = svg.append("g").attr("transform", s"translate(10,30)scale(${scale},${scale})")
    val defs = svg.append("defs")
    defs.append("linearGradient").attr("id", "colors").
      attr("x1", "0%").attr("y1", "0%").attr("x2", "100%").
      attr("y2", "0%").selectAll("stop").data(colors).enter().append("stop").
      attr("offset", (d: String, i: Int) => offsets(i)).attr("stop-color", (d: String) => d).
      attr("stop-opacity", 1)

    defs.append("marker").attr("id", "arrow").
      attr("markerHeight", 4).attr("refy", 0).
      attr("orient", "auto").attr("viewBox", "0 -5 10 10").append("path").
      attr("d", "M0,-3L3,0L0,3").style("fill", "#002060")

    lengendSvg.append("rect").attr("x", 0).attr("width", totalWidth).attr("height", 50).
      style("fill", "url(#colors)")

    val xScale = d3.scale.linear().range(js.Array(0, totalWidth)).domain(js.Array(0, 1))
    val xAxis = d3.svg.axis().orient("bottom").tickValues(js.Array(0, 0.25, 0.5, 0.75, 1).asInstanceOf[js.Array[js.Any]])
      .tickFormat(
        (v) => {
          v.toString
        }
      ).scale(xScale)
    val xAxisSvg = lengendSvg.append("g").attr("class", "axis").attr("transform", s"translate(0,100)").
      call(xAxis)

    xAxisSvg.select(".axis .domain").attr("d", "M0,0V0H720V0").style("fill", "none").
      style("stroke", "#BACBE9").style("stroke-width", "2px").style("shape-rendering", "crispEdges")
    xAxisSvg.selectAll(".axis line").attr("y2", "-50").style("stroke", "#000000").
      style("stroke-width", 5).attr("x2", "0").attr("y1", "0").
      attr("stroke-dasharray", "10,30")
    xAxisSvg.select(".axis line").attr("x1", "2").attr("x2", "2")
    xAxisSvg.selectAll(".axis line").filter { (d: js.Any, i: Int) =>
      i == 4
    }.attr("x1", "-3").attr("x2", "-3")

    lengendSvg.append("line").attr("x1", 0).attr("y1", 75).
      attr("x2", scoreWidth.toDouble).attr("y2", 75)
      .style("stroke", "#002060").style("stroke-width", 9).
      style("marker-end", "url(#arrow)")


  }

}
