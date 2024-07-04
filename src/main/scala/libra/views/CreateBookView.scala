package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.Pages.*
import libra.Routes.*
import libra.entities.*
import libra.http.HttpClient
import libra.utils.Misc
import libra.utils.Misc.transformToOption
import org.scalajs.dom

import scala.concurrent.*
import scala.util.*

/** Представление страницы создания (добавления) книги. */
class CreateBookView(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val url        = libra.App.apiPath + "/books"

  /** Рендер страницы создания (добавления) книги. */
  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        cls := "block-create-book",
        h1("Создание книги"),
        renderInfoInputs,
        renderCreateButton
      )
    )

  private val titleVar       = Var("")
  private val authorNameVar  = Var("")
  private val isbnVar        = Var("")
  private val isbn13Var      = Var("")
  private val editionVar     = Var("")
  private val yearVar        = Var("")
  private val pagesVar       = Var("")
  private val descriptionVar = Var("")
  private val languageVar    = Var("")
  private val categoryVar    = Var("")

  /** Рендер полей ввода информации о книге. */
  private def renderInfoInputs: HtmlElement =
    div(
      renderTextInput(titleVar, "Название*"),
      renderTextInput(authorNameVar, "Автор*"),
      renderIsbnInput(isbnVar, "ISBN (10-значный)", 10),
      renderIsbnInput(isbn13Var, "ISBN (13-значный)", 13),
      renderTextInput(editionVar, "Издание"),
      renderNumberInput(yearVar, "Год издания"),
      renderNumberInput(pagesVar, "Количество страниц"),
      renderTextInput(descriptionVar, "Описание"),
      renderTextInput(languageVar, "Язык издания"),
      renderTextInput(categoryVar, "Категория")
    )

  /** Рендер поля ввода текстовой информации. */
  private def renderTextInput(
      fieldVar: Var[String],
      description: String
  ): HtmlElement =
    div(
      label(description),
      input(
        cls := "input input-create-book",
        onInput.mapToValue --> fieldVar
      )
    )

  /** Рендер поля ввода ISBN. */
  private def renderIsbnInput(
      fieldVar: Var[String],
      description: String,
      length: Int
  ): HtmlElement =
    div(
      label(description),
      input(
        cls := "input input-create-book",
        maxLength(length),
        controlled(
          value <-- fieldVar,
          onInput.mapToValue.filter(_.forall(Character.isDigit)) --> fieldVar
        )
      )
    )

  /** Рендер поля ввода чисел (год, количество страниц, etc.). */
  private def renderNumberInput(
      fieldVar: Var[String],
      description: String
  ): HtmlElement =
    div(
      label(description),
      input(
        cls := "input input-create-book",
        controlled(
          value <-- fieldVar,
          onInput.mapToValue.filter(_.forall(Character.isDigit)) --> fieldVar
        )
      )
    )

  /** Рендер кнопки создания книги. */
  private def renderCreateButton: HtmlElement =
    div(
      button(
        cls := "button button-create-book",
        "Создать",
        onClick
          .mapTo(
            Try(
              Book(
                None,
                titleVar.now(),
                isbnVar.now().transformToOption,
                isbn13Var.now().transformToOption,
                editionVar.now().transformToOption,
                yearVar.now().toIntOption,
                pagesVar.now().toIntOption,
                image = None,
                descriptionVar.now().transformToOption,
                languageVar.now().transformToOption,
                categoryVar.now().transformToOption,
                None,
                Some(Author(None, authorNameVar.now(), None))
              )
            )
          )
          .flatMap {
            case Success(book)      =>
              Misc.getJwt
                .map(jwt => EventStream.fromFuture(httpClient.post[Book, Book](url, jwt, book)))
                .getOrElse(EventStream.fromValue(Left(Exception("Необходима аутентификация"))))
            case Failure(exception) =>
              EventStream.fromFuture(Future.successful(Left(exception)))
          } --> {
          case Left(err: Throwable) => dom.window.alert(err.toString)
          case Right(value)         => router.pushState(BooksPage())
        }
      )
    )

end CreateBookView
