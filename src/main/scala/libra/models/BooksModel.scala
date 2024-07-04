package libra.models

import com.raquo.laminar.api.L.{*, given}

import java.util.UUID

class BooksModel extends Model:

  type DataType = BooksModel.BookRecord
  type DataList = List[DataType]

  val dataVar: Var[DataList]             = Var(List())
  val dataSignal: StrictSignal[DataList] = dataVar.signal

  override def getDataItem(id: UUID): Option[BooksModel.BookRecord] =
    dataVar.now().find(item => item.id == id)

  override def addDataItem(item: DataType): Unit =
    dataVar.update(data => data :+ item)

  override def removeDataItem(id: UUID): Unit =
    dataVar.update(data => data.filter(_.id != id))

end BooksModel

object BooksModel:

  final case class BookRecord(
      id: UUID,
      title: String,
      isbn: String,
      isbn13: String,
      edition: String,
      year: String,
      pages: String,
      image: String,
      description: String,
      language: String,
      category: String,
      publisher: PublishersModel.PublisherRecord,
      author: AuthorsModel.AuthorRecord
  )

  object BookRecord:

    def apply(): BookRecord =
      BookRecord("")

    def apply(title: String): BookRecord =
      val uuid = UUID.randomUUID()
      BookRecord(
        uuid,
        title,
        isbn = "",
        isbn13 = "",
        edition = "",
        year = "",
        pages = "",
        image = "",
        description = "",
        language = "",
        category = "",
        publisher = PublishersModel.PublisherRecord(),
        author = AuthorsModel.AuthorRecord()
      )

  end BookRecord

end BooksModel
