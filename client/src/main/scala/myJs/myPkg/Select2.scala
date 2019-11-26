package myJs.myPkg

import org.querki.jsext._

import scala.scalajs.js
import org.querki.jquery.JQuery

import scala.language.implicitConversions
import scala.scalajs.js.JSConverters._


/**
  * Created by yz on 2019/3/14
  */
trait Select2JQuery extends js.Object {

  def select2(options: Select2Options): JQuery = scalajs.js.native

  def select2(): JQuery = scalajs.js.native

  def select2(method:String): JQuery = scalajs.js.native

  def select2(method:String,data:js.Any,b:Boolean): JQuery = scalajs.js.native

  def select2(method:String,data:js.Any): JQuery = scalajs.js.native

}

object Select2Options extends Select2OptionsBuilder(noOpts)

class Select2OptionsBuilder(val dict: OptMap) extends JSOptionBuilder[Select2Options, Select2OptionsBuilder](new Select2OptionsBuilder(_)) {

  def placeholder(v: String) = jsOpt("placeholder", v)

  def allowClear(v: Boolean) = jsOpt("allowClear", v)

  def data(v: js.Array[String]) = jsOpt("data", v)

  def dropdownParent(v: JQuery) = jsOpt("dropdownParent", v)

  def multiple(v: Boolean) = jsOpt("multiple", v)

  def minimumResultsForSearch(v: Int) = jsOpt("minimumResultsForSearch", v)

}

trait Select2Options extends js.Object {

}


trait Select2JQueryImplicits {
  implicit def implicitSelect2JQuery(jq: JQuery) = {
    jq.asInstanceOf[Select2JQuery]
  }
}
