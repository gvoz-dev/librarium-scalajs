package libra.views

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import libra.Pages.*
import libra.Routes.*
import libra.entities.Book
import libra.http.HttpClient
import libra.models.BooksModel
import libra.models.BooksModel.BookRecord
import libra.utils.Misc
import org.scalajs.dom
import org.scalajs.dom.HTMLButtonElement

import java.util.UUID
import scala.concurrent.*

/** Представление страницы книг. */
class BooksView(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val url        = libra.App.apiPath + "/books"
  private val model      = new BooksModel

  /** [[EventStream]] получения списка книг. */
  private val getBooksStream: EventStream[Either[Throwable, List[Book]]] =
    EventStream.fromFuture(httpClient.get[List[Book]](url))

  /** Рендер страницы книг. */
  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        child <-- getBooksStream.splitEither(
          (err, _) => div(s"Ошибка загрузки страницы: $err"),
          (authors, _) =>
            model.init(authors.map(_.toModelRecord))
            div(
              renderBooksTable,
              renderCreateBookButton
            )
        )
      )
    )

  /** Рендер таблицы книг. */
  private def renderBooksTable: Element =
    table(
      thead(
        tr(
          th("Название"),
          th("Автор"),
          th("Описание"),
          th("ISBN"),
          th("ISBN13"),
          th("Издатель"),
          th("Год издания")
        )
      ),
      tbody(
        children <-- model.dataSignal.split(_.id) { (id, _, itemSignal) =>
          renderBook(id, itemSignal)
        }
      )
    )

  /** Рендер книги как элемента таблицы. */
  private def renderBook(
      id: UUID,
      itemSignal: Signal[BookRecord]
  ): Element =
    tr(
      td(renderReadOnlyInput(itemSignal.map(_.title))),
      td(renderReadOnlyInput(itemSignal.map(_.author.name))),
      td(renderReadOnlyInput(itemSignal.map(_.description))),
      td(renderReadOnlyInput(itemSignal.map(_.isbn))),
      td(renderReadOnlyInput(itemSignal.map(_.isbn13))),
      td(renderReadOnlyInput(itemSignal.map(_.publisher.name))),
      td(renderReadOnlyInput(itemSignal.map(_.year))),
      td(renderDeleteBookButton(id)),
      td(renderUpdateBookButton(id))
    )

  /** Рендер поля ввода для строк (только для чтения). */
  private def renderReadOnlyInput(valueSignal: Signal[String]): Input =
    input(
      cls      := "input input-books",
      readOnly := true,
      value <-- valueSignal
    )

  /** [[EventStream]] удаления книги.
    *
    * @param id
    *   уникальный идентификатор книги
    */
  private def deleteBookStream(id: UUID): EventStream[Option[Throwable]] =
    val urlSlashId = url + "/" + id.toString
    Misc.getJwt
      .map(jwt => EventStream.fromFuture(httpClient.delete(urlSlashId, jwt)))
      .getOrElse(EventStream.fromValue(Some(Exception("Необходима аутентификация"))))
  end deleteBookStream

  /** Рендер кнопки удаления книги.
    *
    * @param id
    *   уникальный идентификатор книги
    */
  private def renderDeleteBookButton(id: UUID): Button =
    button(
      cls := "button button-books-red",
      "Удалить",
      onClick.flatMapTo(deleteBookStream(id)) --> {
        case Some(err: Throwable) => dom.window.alert(err.getMessage)
        case _                    => model.removeDataItem(id)
      }
    )

  /** Рендер кнопки изменения книги.
    *
    * @param id
    *   уникальный идентификатор книги
    */
  private def renderUpdateBookButton(id: UUID): Button =
    import com.raquo.laminar.api.features.unitArrows
    button(
      cls := "button button-books-green",
      "Изменить",
      onClick --> {
        dom.window.alert("Функция не реализована")
      }
    )

  /** Рендер кнопки создания (добавления) книги. */
  private def renderCreateBookButton: HtmlElement =
    import com.raquo.laminar.api.features.unitArrows
    div(
      button(
        cls := "button button-books",
        "Добавить новую книгу",
        onClick --> { router.pushState(CreateBookPage()) }
      )
    )

end BooksView
