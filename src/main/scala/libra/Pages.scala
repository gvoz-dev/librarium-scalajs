package libra

import io.circe.*
import io.circe.generic.semiauto.*

object Pages:

  /** ADT страниц для роутинга. */
  sealed trait Page(val title: String)
  case class HomePage() extends Page("Librarium")
  case class LoginPage() extends Page("Login")
  case class RegistrationPage() extends Page("Registration")
  case class NotFoundPage() extends Page("404 Not Found")

  given Codec[Page] = deriveCodec
  given Codec[HomePage] = deriveCodec
  given Codec[LoginPage] = deriveCodec
  given Codec[RegistrationPage] = deriveCodec
  given Codec[NotFoundPage] = deriveCodec

end Pages
