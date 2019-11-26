package myJs.myPkg

import scala.math.BigDecimal.RoundingMode

/**
  * Created by yz on 2019/3/27
  */
object Implicits extends BootstrapTableJQueryImplicits with Select2JQueryImplicits with DatepickerJQueryImplicits {

  implicit class MyString(v: String) {

    def isBlank = {
      v.trim.isEmpty
    }

  }


}
