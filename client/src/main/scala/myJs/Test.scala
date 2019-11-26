package myJs


import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import org.querki.jquery._
import org.singlespaced.d3js.d3
import org.singlespaced.d3js.Ops._
import scalatags.Text.all._

import scala.scalajs.js
import js.JSConverters._


/**
  * Created by yz on 2019/4/15
  */
@JSExportTopLevel("Test")
object Test {

  @JSExport("init")
  def init = {
    refreshSvg

  }

  def refreshSvg = {

    val totalWidth = 720
    val testData = 0.7
    val lw = "0.5"
    val colors = js.Array("#03FF00", "#D6FF00", "#FFEC00", "#FF9B00", "#FF1A00", "#C20000")
    val offsets = js.Array(0, 0.3, 0.4, 0.6, 0.85, 1)
    val tickts = offsets.drop(1).dropRight(1)
    val svg = d3.select("#svg").append("svg").attr("width", 1000).
      attr("height", 500)
    val lengendSvg = svg.append("g").attr("transform", s"translate(10,30)")
    val defs = svg.append("defs")
    defs.append("linearGradient").attr("id", "colors").
      attr("x1", "0%").attr("y1", "0%").attr("x2", "100%").
      attr("y2", "0%").selectAll("stop").data(colors).enter().append("stop").
      attr("offset", (d: String, i: Int) => offsets(i)).attr("stop-color", (d: String) => d).
      attr("stop-opacity", 1)

    defs.append("marker").attr("id", "arrow").attr("markerWidth", 3).
      attr("markerHeight", 4).attr("refx", 5).attr("refy", 0).
      attr("orient", "auto").attr("viewBox", "0 -5 10 10").append("path").
      attr("d", "M0,-3L3,0L0,3").style("fill", "#002060")

    lengendSvg.append("rect").attr("x", 0).attr("width", totalWidth).attr("height", 50).
      style("fill", "url(#colors)")

    //    lengendSvg.selectAll("line").data(tickts).enter().append("line").
    //      attr("x1", (d: Double, i: Int) => totalWidth * d).attr("y1", 0).
    //      attr("x2", (d: Double, i: Int) => totalWidth * d).attr("y2", 50)
    //      .style("stroke", "black").style("stroke-width", lw)

    val xScale = d3.scale.linear().range(js.Array(0, totalWidth)).domain(js.Array(0, 1))
    val xAxis = d3.svg.axis().orient("bottom").tickValues(js.Array(0, 0.25, 0.5, 0.75, 1).asInstanceOf[js.Array[js.Any]])
      .tickFormat(
        (v) => {
          v.toString
        }
      ).scale(xScale)
    val xAxisSvg=lengendSvg.append("g").attr("class", "axis").attr("transform", s"translate(0,100)").
      call(xAxis)

    xAxisSvg.select(".axis .domain").attr("d","M0,0V0H720V0")
    xAxisSvg.selectAll(".axis line").attr("y2","-50").style("stroke","#000000").
      style("stroke-width",5).attr("x2","0").attr("y1","0").
      attr("stroke-dasharray","10,30")
    xAxisSvg.select(".axis line").attr("x1","2").attr("x2","2")
    xAxisSvg.selectAll(".axis line").filter{(d:js.Any,i:Int)=>
      println(i)
      i==4}.attr("x1","-3").attr("x2","-3")




    //    val titleDict = js.Dictionary("0.15" -> "Ctr", "0.35" -> "S0", "0.5" -> "S0-2", "0.725" -> "S3-4", "0.925" -> "Cirr")
    //    val titleScale = d3.scale.linear().range(js.Array(0, totalWidth)).domain(js.Array(0, 1))
    //    val titleAxis = d3.svg.axis().orient("top").tickValues(titleDict.keySet.toJSArray.asInstanceOf[js.Array[js.Any]])
    //      .tickFormat(
    //        (v) => {
    //          val tmpV = v.toString
    //          titleDict(tmpV)
    //        }
    //      ).scale(xScale)
    //    lengendSvg.append("g").attr("class", "axis").attr("transform", s"translate(0,0)").
    //      call(titleAxis)

    //    lengendSvg.append("line").attr("x1", testData*totalWidth).attr("y1", -3).
    //      attr("x2", testData*totalWidth).attr("y2", 53)
    //      .style("stroke", "black").style("stroke-width", 6)
    lengendSvg.append("line").attr("x1", 0).attr("y1", 75).
      attr("x2", 500).attr("y2", 75)
      .style("stroke", "#002060").style("stroke-width", 9).
      style("marker-end", "url(#arrow)")


  }

}
