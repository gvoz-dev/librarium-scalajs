package libra

import io.circe.*
import io.circe.generic.semiauto.*

package object http:

  /** Тело HTTP-ответа с ошибкой.
    *
    * @param message
    *   сообщение
    */
  final case class ResponseError(message: String) extends Throwable(message)

  given Decoder[ResponseError] = deriveDecoder[ResponseError]

  /** Учётные данные пользователя.
    *
    * @param email
    *   адрес электронной почты
    * @param password
    *   пароль
    */
  final case class Credentials(email: String, password: String):

    /** Учётные данные пользователя валидны. */
    def isValid: Boolean =
      email.nonEmpty && email.contains('@') && password.nonEmpty

  end Credentials

  given Encoder[Credentials] = deriveEncoder[Credentials]

  /** Токен аутентификации.
    *
    * @param jwt
    *   JSON Web Token
    */
  final case class Token(jwt: String)

  given Decoder[Token] = deriveDecoder[Token]

end http
