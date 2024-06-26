package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*
import libra.models.PublishersModel.PublisherRecord

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
):

  def toModelRecord: PublisherRecord =
    val uuid = id.getOrElse(UUID.randomUUID())
    PublisherRecord(uuid, name, country)

end Publisher

object Publisher:

  given Codec[Publisher] = deriveCodec

  def fromModelRecord(record: PublisherRecord): Publisher =
    Publisher(Some(record.id), record.name, record.country)
  end fromModelRecord

end Publisher
