package libra.views

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import libra.entities.Author
import libra.http.HttpClient
import libra.models.AuthorsModel
import libra.models.AuthorsModel.AuthorRecord
import org.scalajs.dom
import org.scalajs.dom.HTMLButtonElement

import java.util.UUID
import scala.concurrent.*

// TODO: Временно эта страница используется только для экспериментов со Scala.js и Laminar!
case class AuthorsView()(using ExecutionContext) extends View:

  private enum ActiveButton:
    case ADD, SAVE

  private val httpClient                         = HttpClient()
  private val url                                = libra.App.apiPath + "/authors"
  private val activeButtonVar: Var[ActiveButton] = Var(ActiveButton.ADD)
  private val model                              = new AuthorsModel

  import model.*

  private val getAuthorsStream: EventStream[Either[Throwable, List[Author]]] =
    EventStream.fromFuture(httpClient.get[List[Author]](url))

  private def postAuthorStream(author: Author): EventStream[Either[Throwable, Author]] =
    val jwt = dom.window.localStorage.getItem("jwt")
    EventStream.fromFuture(httpClient.post[Author, Author](url, jwt, author))
  end postAuthorStream

  private def deleteAuthorStream(id: UUID): EventStream[Option[Throwable]] =
    val urlWithId = url + "/" + id.toString
    val jwt       = dom.window.localStorage.getItem("jwt")
    EventStream.fromFuture(httpClient.delete(urlWithId, jwt))
  end deleteAuthorStream

  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        child <-- getAuthorsStream.splitEither(
          (err, _) => div(s"Ошибка загрузки страницы: $err"),
          (authors, _) =>
            init(authors.map(_.toModelRecord))
            renderDataTable()
        )
      )
    )

  private def renderDataTable(): HtmlElement =
    table(
      thead(tr(th("ID"), th("Name"), th("Country"))),
      tbody(
        children <-- dataSignal.split(_.id) { (id, _, itemSignal) =>
          renderDataItem(id, itemSignal)
        }
      ),
      tfoot(
        tr(
          td(
            child <-- activeButtonVar.signal.map {
              case ActiveButton.ADD  => enabledAddButton
              case ActiveButton.SAVE => disabledAddButton
            }
          ),
          td(
            child <-- activeButtonVar.signal.map {
              case ActiveButton.SAVE => enabledSaveButton
              case ActiveButton.ADD  => disabledSaveButton
            }
          )
        )
      )
    )

  private def renderDataItem(
      id: UUID,
      itemSignal: Signal[AuthorRecord]
  ): HtmlElement =
    tr(
      td(
        input(
          typ      := "text",
          disabled := true,
          value <-- itemSignal.map(_.id.toString)
        )
      ),
      td(
        inputForString(
          itemSignal.map(_.name),
          dataItemUpdater(id, { (item, newName) => item.copy(name = newName) })
        )
      ),
      td(
        inputForString(
          itemSignal.map(_.country),
          dataItemUpdater(id, { (item, newCountry) => item.copy(country = newCountry) })
        )
      ),
      td(
        button(
          "🗑️",
          onClick.flatMap(_ => deleteAuthorStream(id)) --> {
            case Some(err: Throwable) => dom.window.alert(err.getMessage)
            case _                    => removeDataItem(id)
          }
        )
      )
    )

  private def inputForString(
      valueSignal: Signal[String],
      valueUpdater: Observer[String]
  ): Input =
    input(
      typ := "text",
      value <-- valueSignal,
      onInput.mapToValue --> valueUpdater
    )

  private def dataItemUpdater[A](
      id: UUID,
      f: (AuthorRecord, A) => AuthorRecord
  ): Observer[A] =
    dataVar.updater { (data, newValue) =>
      data.map { item =>
        if item.id == id then f(item, newValue) else item
      }
    }

  private def enabledAddButton: Button =
    button(
      "Добавить",
      onClick --> { _ =>
        addDataItem(AuthorRecord())
        activeButtonVar.set(ActiveButton.SAVE)
      }
    )

  private def disabledAddButton: Button =
    button(
      "Добавить",
      disabled := true
    )

  private def enabledSaveButton: Button =
    button(
      "Сохранить",
      onClick.flatMap { _ =>
        val author = Author.fromModelRecord(dataVar.now().last)
        postAuthorStream(author)
      } --> {
        case Left(err: Throwable)  =>
          dom.window.alert(err.getMessage)
        case Right(author: Author) =>
          updateLastDataItem(author.toModelRecord)
          activeButtonVar.set(ActiveButton.ADD)
      }
    )

  private def disabledSaveButton: Button =
    button(
      "Сохранить",
      disabled := true
    )

end AuthorsView
