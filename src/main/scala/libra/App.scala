package libra

import com.raquo.laminar.api.L.{*, given}
import libra.Pages.*
import libra.Routes.*
import org.scalajs.dom

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

/** Точка входа в приложение. */
object App:

  given ExecutionContext = JSExecutionContext.queue

  private val app: HtmlElement =
    div(
      child <-- router.currentPageSignal.map(renderPage)
    )

  def main(args: Array[String]): Unit =
    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      app
    )

end App
