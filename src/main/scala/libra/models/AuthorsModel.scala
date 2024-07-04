package libra.models

import com.raquo.laminar.api.L.{*, given}

import java.util.UUID

class AuthorsModel extends Model:

  type DataType = AuthorsModel.AuthorRecord
  type DataList = List[DataType]

  val dataVar: Var[DataList]             = Var(List())
  val dataSignal: StrictSignal[DataList] = dataVar.signal

  override def getDataItem(id: UUID): Option[AuthorsModel.AuthorRecord] =
    dataVar.now().find(item => item.id == id)

  override def addDataItem(item: DataType): Unit =
    dataVar.update(data => data :+ item)

  override def removeDataItem(id: UUID): Unit =
    dataVar.update(data => data.filter(_.id != id))

end AuthorsModel

object AuthorsModel:

  final case class AuthorRecord(
      id: UUID,
      name: String,
      country: String,
      stored: Boolean
  ):

    def isEmpty: Boolean =
      name.isEmpty && country.isEmpty

  end AuthorRecord

  object AuthorRecord:

    def apply(): AuthorRecord =
      AuthorRecord("")

    def apply(name: String): AuthorRecord =
      AuthorRecord(name, "")

    def apply(name: String, country: String): AuthorRecord =
      val uuid = UUID.randomUUID()
      AuthorRecord(uuid, name, country, false)

  end AuthorRecord

end AuthorsModel
