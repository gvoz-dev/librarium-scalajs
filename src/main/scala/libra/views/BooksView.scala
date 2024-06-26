package libra.views

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import libra.entities.Book
import libra.http.HttpClient
import libra.models.BooksModel
import libra.models.BooksModel.BookRecord
import org.scalajs.dom
import org.scalajs.dom.HTMLButtonElement

import java.util.UUID
import scala.concurrent.*

case class BooksView()(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val url        = libra.App.apiPath + "/books"
  private val model      = new BooksModel

  import model.*

  private val getBooksStream: EventStream[Either[Throwable, List[Book]]] =
    EventStream.fromFuture(httpClient.get[List[Book]](url))

  private def deleteBookStream(id: UUID): EventStream[Option[Throwable]] =
    val urlWithId = url + "/" + id.toString
    val jwt       = dom.window.localStorage.getItem("jwt")
    EventStream.fromFuture(httpClient.delete(urlWithId, jwt))
  end deleteBookStream

  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        child <-- getBooksStream.splitEither(
          (err, _) => div(s"Ошибка загрузки страницы: $err"),
          (authors, _) =>
            init(authors.map(_.toModelRecord))
            div(
              renderDataTable,
              renderCreateBookButton
            )
        )
      )
    )

  private def renderDataTable: Element =
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
        children <-- dataSignal.split(_.id) { (id, _, itemSignal) =>
          renderDataItem(id, itemSignal)
        }
      )
    )

  private def renderDataItem(
      id: UUID,
      itemSignal: Signal[BookRecord]
  ): Element =
    import com.raquo.laminar.api.features.unitArrows
    tr(
      td(
        input(
          cls      := "input input-books",
          typ      := "text",
          readOnly := true,
          value <-- itemSignal.map(_.title)
        )
      ),
      td(
        input(
          cls      := "input input-books",
          typ      := "text",
          readOnly := true,
          value <-- itemSignal.map(_.author.name)
        )
      ),
      td(
        input(
          cls      := "input input-books",
          typ      := "text",
          readOnly := true,
          value <-- itemSignal.map(_.description)
        )
      ),
      td(
        input(
          cls      := "input input-books",
          typ      := "text",
          readOnly := true,
          value <-- itemSignal.map(_.isbn)
        )
      ),
      td(
        input(
          cls      := "input input-books",
          typ      := "text",
          readOnly := true,
          value <-- itemSignal.map(_.isbn13)
        )
      ),
      td(
        input(
          cls      := "input input-books",
          typ      := "text",
          readOnly := true,
          value <-- itemSignal.map(_.publisher.name)
        )
      ),
      td(
        input(
          cls      := "input input-books",
          typ      := "text",
          readOnly := true,
          value <-- itemSignal.map(_.year.toString)
        )
      ),
      td(
        button(
          cls := "button button-books-red",
          "Удалить",
          onClick.flatMap(_ => deleteBookStream(id)) --> {
            case Some(err: Throwable) => dom.window.alert(err.getMessage)
            case _                    => removeDataItem(id)
          }
        )
      ),
      td(
        button(
          cls := "button button-books-green",
          "Изменить",
          onClick --> { dom.window.alert("Функция не реализована") }
        )
      )
    )

  private def renderCreateBookButton: HtmlElement =
    div(
      button(
        cls := "button button-books",
        "Добавить новую книгу"
      )
    )

end BooksView
