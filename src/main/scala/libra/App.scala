package libra

import com.raquo.laminar.api.L.{*, given}
import libra.views.LoginView
import org.scalajs.dom

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

/** Точка входа в приложение. */
object App:

  given ExecutionContext = JSExecutionContext.queue

  def main(args: Array[String]): Unit =
    renderOnDomContentLoaded(
      dom.document.getElementById("app"),
      LoginView().render
    )

end App
