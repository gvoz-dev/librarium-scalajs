package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*

import java.util.UUID

/** Сущность "Издатель".
  *
  * @param id
  *   уникальный идентификатор
  * @param name
  *   название издательства
  * @param country
  *   страна
  */
final case class Publisher(
    id: Option[UUID],
    name: String,
    country: String
)

object Publisher:

  given Codec[Publisher] = deriveCodec

end Publisher
