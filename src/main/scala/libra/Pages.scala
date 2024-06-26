package libra

import io.circe.*
import io.circe.generic.semiauto.*

/** Страницы клиентского приложения. */
object Pages:

  /** ADT страниц для роутинга. */
  sealed trait Page(val title: String)
  case class HomePage()         extends Page("Librarium")
  case class LoginPage()        extends Page("Login")
  case class RegistrationPage() extends Page("Registration")
  case class NotFoundPage()     extends Page("404 Not Found")
  case class AuthorsPage()      extends Page("Authors")
  case class BooksPage()        extends Page("Books")

  given Codec[Page]             = deriveCodec
  given Codec[HomePage]         = deriveCodec
  given Codec[LoginPage]        = deriveCodec
  given Codec[RegistrationPage] = deriveCodec
  given Codec[NotFoundPage]     = deriveCodec
  given Codec[AuthorsPage]      = deriveCodec
  given Codec[BooksPage]        = deriveCodec

end Pages
