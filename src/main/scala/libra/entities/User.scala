package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*

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
)

object User:

  given Codec[User] = deriveCodec

end User
