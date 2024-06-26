package libra.models

import com.raquo.laminar.api.L.{*, given}

import java.util.UUID

class AuthorsModel extends Model:

  type DataType = AuthorsModel.AuthorRecord
  type DataList = List[DataType]

  val dataVar: Var[DataList]             = Var(List())
  val dataSignal: StrictSignal[DataList] = dataVar.signal

  def addDataItem(item: DataType): Unit =
    dataVar.update(data => data :+ item)

  def removeDataItem(id: UUID): Unit =
    dataVar.update(data => data.filter(_.id != id))

end AuthorsModel

object AuthorsModel:

  case class AuthorRecord(
      id: UUID,
      name: String,
      country: String
  ):

    def isEmptyRecord: Boolean =
      name.isEmpty && country.isEmpty

  end AuthorRecord

  object AuthorRecord:

    def apply(): AuthorRecord =
      AuthorRecord("")

    def apply(name: String): AuthorRecord =
      AuthorRecord(name, "")

    def apply(name: String, country: String): AuthorRecord =
      val uuid = UUID.randomUUID()
      AuthorRecord(uuid, name, country)

  end AuthorRecord

end AuthorsModel
