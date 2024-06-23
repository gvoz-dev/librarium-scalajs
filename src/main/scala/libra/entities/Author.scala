package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*

import java.util.UUID

/** Сущность "Автор".
  *
  * @param id
  *   уникальный идентификатор
  * @param name
  *   имя автора
  * @param country
  *   страна автора
  */
final case class Author(
    id: Option[UUID],
    name: String,
    country: Option[String]
)

object Author:

  given Codec[Author] = deriveCodec

end Author
