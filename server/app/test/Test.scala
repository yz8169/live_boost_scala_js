package test

import shared.ResultData
import utils.DesEncrypter

import scala.collection.mutable.ArrayBuffer

/**
  * Created by yz on 2018/9/3
  */
object Test {

  def main(args: Array[String]): Unit = {

    val data = ResultData(67.4, 3, 7.5)

    val normal1 = (100 - data.caseDouble) / 100
    val fiber = (100 - data.cirrhosis) / 100
    val early = (100 - data.lateFibrosis) / 100
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

    println(trueScore)


  }

}
