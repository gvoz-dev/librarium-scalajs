package libra.models

import com.raquo.laminar.api.L.{*, given}

import java.util.UUID

class PublishersModel extends Model:

  type DataType = PublishersModel.PublisherRecord
  type DataList = List[DataType]

  val dataVar: Var[DataList]             = Var(List())
  val dataSignal: StrictSignal[DataList] = dataVar.signal

  override def getDataItem(id: UUID): Option[PublishersModel.PublisherRecord] =
    dataVar.now().find(item => item.id == id)

  override def addDataItem(item: DataType): Unit =
    dataVar.update(data => data :+ item)

  override def removeDataItem(id: UUID): Unit =
    dataVar.update(data => data.filter(_.id != id))

end PublishersModel

object PublishersModel:

  final case class PublisherRecord(
      id: UUID,
      name: String,
      country: String
  ):

    def isEmpty: Boolean =
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
