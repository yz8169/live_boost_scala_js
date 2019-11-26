package shared

import scala.math.BigDecimal.RoundingMode

/**
  * Created by yz on 2019/4/24
  */
object Implicits {

  implicit class MyBigDecimal(x:BigDecimal){

    def toFixed(maxScale:Int)={
      val scale = x.scale
      val finalScale = if (scale >= maxScale) maxScale else if (scale >= 0) scale else 0
      x.setScale(finalScale, RoundingMode.HALF_UP)
    }

  }


}
