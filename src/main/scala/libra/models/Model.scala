package libra.models

import com.raquo.laminar.api.L.{*, given}

import java.util.UUID

/** Модель, предоставляющая данные и методы для работы с ними. */
trait Model:

  type DataType
  type DataList = List[DataType]

  val dataVar: Var[DataList]
  val dataSignal: StrictSignal[DataList]

  /** Инициализировать модель.
    *
    * @param items
    *   список элементов данных
    */
  def init(items: DataList): Unit =
    dataVar.set(items)

  /** Обновить последний элемент данных из модели. */
  def updateLastDataItem(item: DataType): Unit =
    dataVar.update(_.dropRight(1))
    addDataItem(item)

  /** Добавить элемент данных в модель.
    *
    * @param item
    *   элемент данных
    */
  def addDataItem(item: DataType): Unit

  /** Удалить элемент данных из модели.
    *
    * @param id
    *   уникальный идентификатор элемента
    */
  def removeDataItem(id: UUID): Unit

end Model
