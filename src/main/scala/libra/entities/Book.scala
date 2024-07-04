package libra.entities

import io.circe.*
import io.circe.generic.semiauto.*
import libra.entities.*
import libra.models.AuthorsModel.AuthorRecord
import libra.models.BooksModel.BookRecord
import libra.models.PublishersModel.PublisherRecord

import java.util.UUID

/** Сущность "Книга".
  *
  * @param id
  *   уникальный идентификатор
  * @param title
  *   название книги
  * @param isbn
  *   международный стандартный книжный номер (10-значный)
  * @param isbn13
  *   международный стандартный книжный номер (13-значный)
  * @param edition
  *   издание
  * @param year
  *   год издания
  * @param pages
  *   количество страниц
  * @param image
  *   изображение обложки
  * @param description
  *   описание книги
  * @param language
  *   язык текста
  * @param category
  *   категория
  * @param publisher
  *   издатель
  * @param author
  *   автор
  */
final case class Book(
    id: Option[UUID],
    title: String,
    isbn: Option[String],
    isbn13: Option[String],
    edition: Option[String],
    year: Option[Int],
    pages: Option[Int],
    image: Option[String],
    description: Option[String],
    language: Option[String],
    category: Option[String],
    publisher: Option[Publisher],
    author: Option[Author]
):

  def toModelRecord: BookRecord =
    val uuid = id.getOrElse(UUID.randomUUID())
    BookRecord(
      uuid,
      title,
      isbn.getOrElse(""),
      isbn13.getOrElse(""),
      edition.getOrElse(""),
      year.map(_.toString).getOrElse(""),
      pages.map(_.toString).getOrElse(""),
      image.getOrElse(""),
      description.getOrElse(""),
      language.getOrElse(""),
      category.getOrElse(""),
      publisher.map(_.toModelRecord).getOrElse(PublisherRecord()),
      author.map(_.toModelRecord).getOrElse(AuthorRecord())
    )

end Book

object Book:

  given Codec[Book] = deriveCodec

  def fromModelRecord(record: BookRecord): Book =
    import libra.utils.Misc.transformToOption

    val publisher =
      if record.publisher.isEmpty then None
      else Some(Publisher.fromModelRecord(record.publisher))

    val author =
      if record.publisher.isEmpty then None
      else Some(Author.fromModelRecord(record.author))

    Book(
      Some(record.id),
      record.title,
      record.isbn.transformToOption,
      record.isbn13.transformToOption,
      record.edition.transformToOption,
      record.year.toIntOption,
      record.pages.toIntOption,
      record.image.transformToOption,
      record.description.transformToOption,
      record.language.transformToOption,
      record.category.transformToOption,
      publisher,
      author
    )
  end fromModelRecord

end Book
