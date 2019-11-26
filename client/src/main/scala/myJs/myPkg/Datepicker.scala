package myJs.myPkg

import org.querki.jsext._

import scala.scalajs.js
import org.querki.jquery.JQuery

import scala.language.implicitConversions
import scala.scalajs.js.JSConverters._


/**
  * Created by yz on 2019/3/14
  */
trait DatepickerJQuery extends js.Object {

  def datepicker(options: DatepickerOptions): JQuery = scalajs.js.native

}

object DatepickerOptions extends DatepickerOptionsBuilder(noOpts)

class DatepickerOptionsBuilder(val dict: OptMap) extends JSOptionBuilder[DatepickerOptions, DatepickerOptionsBuilder](new DatepickerOptionsBuilder(_)) {

  def format(v: String) = jsOpt("format", v)

  def language(v: String) = jsOpt("language", v)


}

trait DatepickerOptions extends js.Object {

}


trait DatepickerJQueryImplicits {
  implicit def implicitDatepickerJQuery(jq: JQuery) = {
    jq.asInstanceOf[DatepickerJQuery]
  }
}
