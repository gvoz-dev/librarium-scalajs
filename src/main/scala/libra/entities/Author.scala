package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*
import libra.models.AuthorsModel.AuthorData

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

  def toModelData: AuthorData =
    if id.isDefined then AuthorData(id.get, name, country.getOrElse(""))
    else AuthorData(name, country.getOrElse(""))

end Author

object Author:

  def fromModelData(authorData: AuthorData): Author =
    val country =
      if authorData.country.isEmpty then None
      else Some(authorData.country)
    Author(Some(authorData.id), authorData.name, country)
  end fromModelData

  given Codec[Author] = deriveCodec

end Author
