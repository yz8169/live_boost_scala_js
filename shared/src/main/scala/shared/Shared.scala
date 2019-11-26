package shared

import scala.math.BigDecimal.RoundingMode
import Implicits._

/**
  * Created by yz on 2019/4/24
  */

case class ResultData(caseDouble: BigDecimal, cirrhosis: BigDecimal, lateFibrosis: BigDecimal)


object Shared {

  val userStr="boost_user"
  val adminStr="boost_admin"

  def calculateScore(resultData: ResultData) = {
    val caseDouble = resultData.caseDouble
    val cirrhosis = resultData.cirrhosis
    val lateFibrosis = resultData.lateFibrosis
    val normal1 = (100 - caseDouble) / 100
    val fiber = (100 - cirrhosis) / 100
    val early = (100 - lateFibrosis) / 100
    val score = if (normal1 > 0.5) {
      1 - normal1
    } else {
      if (fiber <= 0.5) {
        0.5 + 0.5 * (1 - fiber)
      } else {
        0.5 + 0.25 * (1 - early)
      }
    }

    def linear(from: (Double, Double), to: (Double, Double))(v: BigDecimal) = {
      val width1 = from._2 - from._1
      val width2 = to._2 - to._1
      val rate = width2 / width1
      to._1 + rate * (v - from._1)
    }

    val trueScore = score match {
      case x if x <= 0.5 => linear(from = (0, 0.5), to = (0, 0.25))(x)
      case x if x > 0.5 && x <= 0.625 => linear(from = (0.5, 0.625), to = (0.25, 0.5))(x)
      case x if x > 0.625 && x <= 0.75 => linear(from = (0.625, 0.75), to = (0.5, 0.75))(x)
      case x if x > 0.75 => linear(from = (0.75, 1), to = (0.75, 1))(x)
    }
    trueScore.toFixed(2)

  }

  def getResult(score: BigDecimal) = {
    score match {
      case x if x <= 0.25 => "低慢性肝病风险"
      case x if x > 0.25 && x <= 0.5 => "早期肝纤维化风险"
      case x if x > 0.5 && x <= 0.75 => "晚期肝纤维化风险"
      case x if x > 0.75 => "肝硬化风险"
    }

  }


}
