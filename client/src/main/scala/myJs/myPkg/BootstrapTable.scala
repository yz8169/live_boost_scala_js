package myJs.myPkg

import org.querki.jsext._

import scala.scalajs.js
import org.querki.jquery.JQuery

import scala.language.implicitConversions
import scala.scalajs.js.JSConverters._


/**
  * Created by yz on 2019/3/14
  */
trait BootstrapTableJQuery extends js.Object {

  def bootstrapTable(options: TableOptions): JQuery = scalajs.js.native

  def bootstrapTable(): JQuery = scalajs.js.native

  def bootstrapTable(method: String, parameter: js.Any): JQuery = scalajs.js.native

  def bootstrapTable(method: String): JQuery = scalajs.js.native


}

object TableOptions extends TableOptionsBuilder(noOpts)

class TableOptionsBuilder(val dict: OptMap) extends JSOptionBuilder[TableOptions, TableOptionsBuilder](new TableOptionsBuilder(_)) {

  def method(v: String) = jsOpt("method", v)

  def data(v: js.Any) = jsOpt("data", v)

  def columns(v: js.Array[ColumnOptionsBuilder]) = {
    val fmtV = v.map(_.dict.toJSDictionary)
    jsOpt("columns", fmtV)
  }

  def exportOptions(v: ExportOptions) = jsOpt("exportOptions", v)

  def exportHiddenColumns(v: Boolean) = jsOpt("exportHiddenColumns", v)


}

trait TableOptions extends js.Object {

}

object ExportOptions extends ExportOptionsBuilder(noOpts)

class ExportOptionsBuilder(val dict: OptMap) extends JSOptionBuilder[ExportOptions, ExportOptionsBuilder](new ExportOptionsBuilder(_)) {

  def csvSeparator(v: String) = jsOpt("csvSeparator", v)

  def fileName(v: js.Any) = jsOpt("fileName", v)

  def exportHiddenColumns(v: Boolean) = jsOpt("exportHiddenColumns", v)

  def exportOptions(v: ExportOptions) = jsOpt("exportOptions", v)


}

trait ExportOptions extends js.Object {

}

object ColumnOptions extends ColumnOptionsBuilder(noOpts)

class ColumnOptionsBuilder(val dict: OptMap) extends JSOptionBuilder[ColumnOptions, ColumnOptionsBuilder](new ColumnOptionsBuilder(_)) {

  def field(v: String) = jsOpt("field", v)

  def title(v: String) = jsOpt("title", v)

  def sortable(v: Boolean) = jsOpt("sortable", v)

  def titleTooltip(v: String) = jsOpt("titleTooltip", v)

  def formatter(v: js.Function) = jsOpt("formatter", v)

}

trait ColumnOptions extends js.Object {

}

trait BootstrapTableJQueryImplicits {
  implicit def implicitBootstrapTableJQuery(jq: JQuery): BootstrapTableJQuery = {
    jq.asInstanceOf[BootstrapTableJQuery]
  }
}
