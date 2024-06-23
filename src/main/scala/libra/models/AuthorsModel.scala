package libra.models

import com.raquo.laminar.api.L.{*, given}

import java.util.UUID

class AuthorsModel extends Model:

  type DataType = AuthorsModel.AuthorData
  type DataList = List[DataType]

  val dataVar: Var[DataList] = Var(List())
  val dataSignal: StrictSignal[DataList] = dataVar.signal

  def addDataItem(item: DataType): Unit =
    dataVar.update(data => data :+ item)

  def removeDataItem(id: UUID): Unit =
    dataVar.update(data => data.filter(_.id != id))

end AuthorsModel

object AuthorsModel:

  case class AuthorData(id: UUID, name: String, country: String)

  object AuthorData:

    def apply(): AuthorData =
      val uuid = UUID.randomUUID()
      AuthorData(uuid, "", "")

    def apply(name: String, country: String): AuthorData =
      val uuid = UUID.randomUUID()
      AuthorData(uuid, name, country)

  end AuthorData

end AuthorsModel
