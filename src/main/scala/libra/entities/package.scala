package libra

import io.circe.*
import io.circe.generic.semiauto.*
import libra.utils.Validators

package object entities:

  /** Учётные данные пользователя.
    *
    * @param email
    *   адрес электронной почты
    * @param password
    *   пароль
    */
  final case class Credentials(email: String, password: String):

    /** Произвести валидацию учётных данных пользователя. */
    def isValid: Boolean =
      email.nonEmpty && Validators.validateEmail(email) && password.nonEmpty

  end Credentials

  given Encoder[Credentials] = deriveEncoder[Credentials]

  /** Токен аутентификации.
    *
    * @param jwt
    *   JSON Web Token
    */
  final case class Token(jwt: String)

  given Decoder[Token] = deriveDecoder[Token]

end entities
