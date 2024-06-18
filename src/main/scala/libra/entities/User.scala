package libra.entities

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
    password: String,
    role: String = "user"
)

object User:

  import io.circe.*
  import io.circe.generic.semiauto.*

  given Decoder[User] = deriveDecoder[User]

end User
