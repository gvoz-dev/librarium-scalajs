package libra

import com.raquo.laminar.api.L.{*, given}
import com.raquo.waypoint.*
import io.circe.parser.*
import io.circe.syntax.*
import libra.Pages.*
import libra.views.*

import scala.concurrent.ExecutionContext
import scala.scalajs.concurrent.JSExecutionContext

/** Маршруты клиентского приложения. */
object Routes:

  given ExecutionContext = JSExecutionContext.queue

  private val appRoot = root

  private val routes = List(
    Route.static(HomePage(), appRoot / endOfSegments),
    Route.static(LoginPage(), appRoot / "login" / endOfSegments),
    Route.static(RegistrationPage(), appRoot / "registration" / endOfSegments),
    Route.static(AuthorsPage(), appRoot / "authors" / endOfSegments),
    Route.static(BooksPage(), appRoot / "books" / endOfSegments),
    Route.static(CreateBookPage(), appRoot / "books" / "create" / endOfSegments)
  )

  val router: Router[Page] =
    new Router[Page](
      routes = routes,
      getPageTitle = _.title,
      serializePage = _.asJson.toString,
      deserializePage = decode[Page](_).getOrElse(NotFoundPage()),
      routeFallback = _ => NotFoundPage()
    )(
      popStateEvents = windowEvents(_.onPopState),
      owner = unsafeWindowOwner
    )

  val views: SplitRender[Page, HtmlElement] =
    SplitRender(router.currentPageSignal)
      .collectStatic(HomePage())(HomeView().render)
      .collectStatic(LoginPage())(LoginView().render)
      .collectStatic(RegistrationPage())(RegistrationView().render)
      .collectStatic(AuthorsPage())(AuthorsView().render)
      .collectStatic(BooksPage())(BooksView().render)
      .collectStatic(CreateBookPage())(CreateBookView().render)
      .collectStatic(NotFoundPage())(NotFoundView().render)

end Routes
