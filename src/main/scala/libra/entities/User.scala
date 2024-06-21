package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*
import libra.utils.Validators

import java.util.UUID

/** Сущность "Пользователь".
  *
  * @param id
  *   уникальный идентификатор
  * @param name
  *   имя пользователя
  * @param email
  *   почта
  * @param password
  *   пароль
  * @param role
  *   роль пользователя
  */
final case class User(
    id: Option[UUID],
    name: String,
    email: String,
    password: Option[String],
    role: String = "user"
):

  /** Произвести валидацию данных пользователя. */
  def isValid: Boolean =
    name.nonEmpty && password.exists(_.nonEmpty) && role.nonEmpty
      && email.nonEmpty && Validators.validateEmail(email)

end User

object User:

  given Codec[User] = deriveCodec

end User
