package libra.models

import com.raquo.laminar.api.L.{*, given}

import java.util.UUID

class PublishersModel extends Model:

  type DataType = PublishersModel.PublisherRecord
  type DataList = List[DataType]

  val dataVar: Var[DataList]             = Var(List())
  val dataSignal: StrictSignal[DataList] = dataVar.signal

  def addDataItem(item: DataType): Unit =
    dataVar.update(data => data :+ item)

  def removeDataItem(id: UUID): Unit =
    dataVar.update(data => data.filter(_.id != id))

end PublishersModel

object PublishersModel:

  case class PublisherRecord(
      id: UUID,
      name: String,
      country: String
  ):

    def isEmptyRecord: Boolean =
      name.isEmpty && country.isEmpty

  end PublisherRecord

  object PublisherRecord:

    def apply(): PublisherRecord =
      PublisherRecord("")

    def apply(name: String): PublisherRecord =
      PublisherRecord(name, "")

    def apply(name: String, country: String): PublisherRecord =
      val uuid = UUID.randomUUID()
      PublisherRecord(uuid, name, country)

  end PublisherRecord

end PublishersModel
