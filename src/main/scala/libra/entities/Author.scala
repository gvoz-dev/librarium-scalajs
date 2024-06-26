package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*
import libra.models.AuthorsModel.AuthorRecord

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
):

  def toModelRecord: AuthorRecord =
    val uuid = id.getOrElse(UUID.randomUUID())
    AuthorRecord(uuid, name, country.getOrElse(""))

end Author

object Author:

  given Codec[Author] = deriveCodec

  def fromModelRecord(record: AuthorRecord): Author =
    import libra.utils.Misc.transformToOption
    Author(Some(record.id), record.name, record.country.transformToOption)
  end fromModelRecord

end Author
