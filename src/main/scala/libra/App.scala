package libra

import com.raquo.laminar.api.L.{*, given}
import libra.Routes.*
import org.scalajs.dom

/** Точка входа в Scala.js приложение. */
object App:

  val apiPath: String = "http://localhost:8080/api/v1"

  private val app: HtmlElement =
    div(
      child <-- views.signal
    )

  def main(args: Array[String]): Unit =
    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      app
    )

end App
