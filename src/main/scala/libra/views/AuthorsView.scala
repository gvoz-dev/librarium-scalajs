package libra.views

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import libra.entities.Author
import libra.http.HttpClient
import libra.models.AuthorsModel
import libra.models.AuthorsModel.AuthorData
import org.scalajs.dom
import org.scalajs.dom.HTMLButtonElement

import java.util.UUID
import scala.concurrent.*

case class AuthorsView()(using ExecutionContext) extends View:

  private enum ActiveButton:
    case ADD, SAVE

  private val httpClient = HttpClient()
  private val url = libra.App.apiPath + "/authors"
  private val activeButtonVar: Var[ActiveButton] = Var(ActiveButton.ADD)
  private val model = new AuthorsModel

  import model.*

  private val getAuthorsStream: EventStream[Either[Throwable, List[Author]]] =
    EventStream.fromFuture(httpClient.get[List[Author]](url))

  private def postAuthorStream(
      author: Author
  ): EventStream[Either[Throwable, Author]] =
    val jwt = dom.window.localStorage.getItem("jwt")
    EventStream.fromFuture(httpClient.post[Author, Author](url, jwt, author))
  end postAuthorStream

  private def deleteAuthorStream(
      id: UUID
  ): EventStream[Either[Throwable, Unit]] =
    val urlWithId = url + "/" + id.toString
    val jwt = dom.window.localStorage.getItem("jwt")
    EventStream.fromFuture(httpClient.delete(urlWithId, jwt))
  end deleteAuthorStream

  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        child <-- getAuthorsStream.splitEither(
          (err, _) => div(s"Ошибка загрузки страницы: $err"),
          (authors, _) =>
            init(authors.map(_.toModelData))
            renderDataTable()
        )
      )
    )

  private def renderDataTable(): Element =
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
      itemSignal: Signal[AuthorData]
  ): Element =
    tr(
      td(
        input(
          typ := "text",
          disabled := true,
          value <-- itemSignal.map(_.id.toString)
        )
      ),
      td(
        inputForString(
          itemSignal.map(_.name),
          dataItemUpdater(
            id,
            { (item, newName) => item.copy(name = newName) }
          )
        )
      ),
      td(
        inputForString(
          itemSignal.map(_.country),
          dataItemUpdater(
            id,
            { (item, newCountry) => item.copy(country = newCountry) }
          )
        )
      ),
      td(
        button(
          "🗑️",
          onClick.flatMap(_ => deleteAuthorStream(id)) --> {
            case Left(err: Throwable) => dom.window.alert(err.getMessage)
            case Right(_)             => removeDataItem(id)
          }
        )
      )
    )
  end renderDataItem

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
      f: (AuthorData, A) => AuthorData
  ): Observer[A] =
    dataVar.updater { (data, newValue) =>
      data.map { item =>
        if item.id == id then f(item, newValue) else item
      }
    }

  private def enabledAddButton =
    button(
      "Добавить",
      onClick --> { _ =>
        addDataItem(AuthorData())
        activeButtonVar.set(ActiveButton.SAVE)
      }
    )

  private def disabledAddButton =
    button(
      "Добавить",
      disabled := true
    )

  private def enabledSaveButton =
    button(
      "Сохранить",
      onClick.flatMap { _ =>
        val author = Author.fromModelData(dataVar.now().last)
        postAuthorStream(author)
      } --> {
        case Left(err: Throwable) =>
          dom.window.alert(err.getMessage)
        case Right(author: Author) =>
          updateLastDataItem(author.toModelData)
          activeButtonVar.set(ActiveButton.ADD)
      }
    )

  private def disabledSaveButton =
    button(
      "Сохранить",
      disabled := true
    )

end AuthorsView
