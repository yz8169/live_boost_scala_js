package myJs.myPkg

import org.querki.jsext._

import scala.scalajs.js

/**
  * Created by yz on 2019/3/14
  */
trait Layer extends js.Object {

  def alert(element: String, options: LayerOptions):Int = js.native

  def close(index: Int):Unit = js.native

  def confirm(element: String, options: LayerOptions,yes:js.Function,cancel:js.Function):Int = js.native

  def load(icon: Int, options: LayerOptions):Int = js.native

  def msg(content: String, options: LayerOptions):Int = js.native

}

object LayerOptions extends LayerOptionsBuilder(noOpts)

class LayerOptionsBuilder(val dict: OptMap) extends JSOptionBuilder[LayerOptions, LayerOptionsBuilder](new LayerOptionsBuilder(_)) {

  def title(v: String) = jsOpt("title", v)

  def time(v: Int) = jsOpt("time", v)

  def closeBtn(v: Int) = jsOpt("closeBtn", v)

  def skin(v: String) = jsOpt("skin", v)

  def btn[T](v: js.Array[T]) = jsOpt("btn", v)

  def shade[T](v: js.Array[T]) = jsOpt("shade", v)

  def icon(v: Int) = jsOpt("icon", v)

}

trait LayerOptions extends js.Object {

}