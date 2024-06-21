package libra

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.*
import io.circe.parser.*
import io.circe.syntax.*
import libra.Pages.*
import libra.views.*

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

object Routes:

  given ExecutionContext = JSExecutionContext.queue

  private val appRoot = root

  private val routes = List(
    Route.static(LoginPage(), appRoot / "login" / endOfSegments),
    Route.static(RegistrationPage(), appRoot / "registration" / endOfSegments)
  )

  val router = new Router[Page](
    routes = routes,
    getPageTitle = _.title,
    serializePage = _.asJson.toString,
    deserializePage = decode[Page](_).getOrElse(NotFoundPage()),
    routeFallback = _ => NotFoundPage()
  )(
    popStateEvents = windowEvents(_.onPopState),
    owner = unsafeWindowOwner
  )

  def renderPage(page: Page): HtmlElement =
    page match
      case LoginPage()        => LoginView().render
      case RegistrationPage() => RegistrationView().render
      case _                  => NotFoundView().render

end Routes
