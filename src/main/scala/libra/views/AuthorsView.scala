package libra.views

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import libra.entities.Author
import libra.http.HttpClient
import libra.models.AuthorsModel
import libra.models.AuthorsModel.AuthorRecord
import libra.utils.Misc
import org.scalajs.dom
import org.scalajs.dom.HTMLButtonElement

import java.util.UUID
import scala.concurrent.*

/** Представление страницы авторов. */
class AuthorsView(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val url        = libra.App.apiPath + "/authors"
  private val model      = new AuthorsModel

  /** [[EventStream]] получения списка авторов. */
  private val getAuthorsStream: EventStream[Either[Throwable, List[Author]]] =
    EventStream.fromFuture(httpClient.get[List[Author]](url))

  /** Рендер страницы авторов. */
  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        child <-- getAuthorsStream.splitEither(
          (err, _) => div(s"Ошибка загрузки страницы: $err"),
          (authors, _) =>
            model.init(authors.map(_.toModelRecord))
            div(
              renderAuthorsTable,
              renderAddButton
            )
        )
      )
    )

  /** Рендер таблицы авторов. */
  private def renderAuthorsTable: HtmlElement =
    table(
      thead(
        tr(
          th("ID"),
          th("Name"),
          th("Country")
        )
      ),
      tbody(
        children <-- model.dataSignal.split(_.id) { (id, _, itemSignal) =>
          renderAuthor(id, itemSignal)
        }
      )
    )

  /** Рендер автора как элемента таблицы. */
  private def renderAuthor(
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
        renderInput(
          itemSignal.map(_.name),
          authorUpdater(id, { (item, newName) => item.copy(name = newName) })
        )
      ),
      td(
        renderInput(
          itemSignal.map(_.country),
          authorUpdater(id, { (item, newCountry) => item.copy(country = newCountry) })
        )
      ),
      td(
        renderCreateButton(
          id,
          itemSignal.map(_.stored)
        )
      ),
      td(
        renderUpdateButton(
          id,
          itemSignal.map(_.stored)
        )
      ),
      td(
        renderDeleteButton(
          id,
          itemSignal.map(_.stored)
        )
      )
    )

  /** Рендер поля ввода. */
  private def renderInput(
      valueSignal: Signal[String],
      valueUpdater: Observer[String]
  ): Input =
    input(
      typ := "text",
      value <-- valueSignal,
      onInput.mapToValue --> valueUpdater
    )

  /** [[Observer]] обновления автора. */
  private def authorUpdater[A](
      id: UUID,
      f: (AuthorRecord, A) => AuthorRecord
  ): Observer[A] =
    model.dataVar.updater { (data, newValue) =>
      data.map { item =>
        if item.id == id then f(item, newValue) else item
      }
    }

  /** Рендер кнопки добавления автора. */
  private def renderAddButton: Button =
    button(
      "Добавить нового автора",
      onClick --> { _ =>
        model.addDataItem(AuthorRecord())
      }
    )

  /** [[EventStream]] создания автора.
    *
    * @param author
    *   автор
    */
  private def postAuthorStream(author: Author): EventStream[Either[Throwable, Author]] =
    Misc.getJwt
      .map(jwt => EventStream.fromFuture(httpClient.post[Author, Author](url, jwt, author)))
      .getOrElse(EventStream.fromValue(Left(Exception("Необходима аутентификация"))))
  end postAuthorStream

  /** Рендер кнопки создания автора.
    *
    * @param id
    *   уникальный идентификатор автора
    * @param storedSignal
    *   сигнал свойства "Автор сохранён"
    */
  private def renderCreateButton(
      id: UUID,
      storedSignal: Signal[Boolean]
  ): Button =
    button(
      "Сохранить",
      disabled <-- storedSignal,
      onClick.flatMap { _ =>
        model
          .getDataItem(id)
          .map(authorRecord => postAuthorStream(Author.fromModelRecord(authorRecord)))
          .getOrElse(EventStream.fromValue(Left(Exception("Автор не найден"))))
      } --> {
        case Left(err: Throwable)  =>
          dom.window.alert(err.getMessage)
        case Right(author: Author) =>
          model.updateDataItem(id, author.toModelRecord)
      }
    )

  /** [[EventStream]] обновления автора.
    *
    * @param author
    *   автор
    */
  private def putAuthorStream(author: Author): EventStream[Either[Throwable, Author]] =
    Misc.getJwt
      .map(jwt => EventStream.fromFuture(httpClient.put[Author, Author](url, jwt, author)))
      .getOrElse(EventStream.fromValue(Left(Exception("Необходима аутентификация"))))
  end putAuthorStream

  /** Рендер кнопки обновления автора.
    *
    * @param id
    *   уникальный идентификатор автора
    * @param storedSignal
    *   сигнал свойства "Автор сохранён"
    */
  private def renderUpdateButton(
      id: UUID,
      storedSignal: Signal[Boolean]
  ): Button =
    button(
      "Изменить",
      disabled <-- storedSignal.map(!_),
      onClick.flatMap { _ =>
        model
          .getDataItem(id)
          .map(authorRecord => putAuthorStream(Author.fromModelRecord(authorRecord)))
          .getOrElse(EventStream.fromValue(Left(Exception("Автор не найден"))))
      } --> {
        case Left(err: Throwable)  =>
          dom.window.alert(err.getMessage)
        case Right(author: Author) =>
          model.updateDataItem(id, author.toModelRecord)
      }
    )

  /** [[EventStream]] удаления автора.
    *
    * @param id
    *   уникальный идентификатор автора
    */
  private def deleteAuthorStream(id: UUID): EventStream[Option[Throwable]] =
    val urlSlashId = url + "/" + id.toString
    Misc.getJwt
      .map(jwt => EventStream.fromFuture(httpClient.delete(urlSlashId, jwt)))
      .getOrElse(EventStream.fromValue(Some(Exception("Необходима аутентификация"))))
  end deleteAuthorStream

  /** Рендер кнопки удаления автора.
    *
    * @param id
    *   уникальный идентификатор автора
    * @param storedSignal
    *   сигнал свойства "Автор сохранён"
    */
  private def renderDeleteButton(
      id: UUID,
      storedSignal: Signal[Boolean]
  ): Button =
    button(
      "Удалить",
      onClick.flatMap { _ =>
        storedSignal.flatMapSwitch {
          case true  => deleteAuthorStream(id)
          case false => EventStream.fromValue(None) // Просто удалить из таблицы, если автор не сохранён
        }
      } --> {
        case Some(err: Throwable) => dom.window.alert(err.getMessage)
        case _                    => model.removeDataItem(id)
      }
    )

end AuthorsView
