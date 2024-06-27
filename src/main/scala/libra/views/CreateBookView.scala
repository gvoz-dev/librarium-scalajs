package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.Pages.BooksPage
import libra.Routes.router
import libra.entities.*
import libra.http.HttpClient
import libra.utils.Misc.transformToOption
import org.scalajs.dom

import scala.concurrent.*
import scala.util.*

case class CreateBookView()(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val url        = libra.App.apiPath + "/books"

  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        cls := "block-create-book",
        h1("Создание книги"),
        renderInputFields,
        renderCreateButton
      )
    )

  private val titleVar       = Var("")
  private val isbnVar        = Var("")
  private val isbn13Var      = Var("")
  private val editionVar     = Var("")
  private val yearVar        = Var("")
  private val pagesVar       = Var("")
  private val descriptionVar = Var("")
  private val languageVar    = Var("")
  private val categoryVar    = Var("")
  private val authorNameVar  = Var("")

  private def renderInputFields: HtmlElement =
    div(
      renderInputField("title", titleVar, "Название*"),
      renderInputField("authorName", authorNameVar, "Автор*"),
      renderInputField("isbn", isbnVar, "ISBN (10-значный)"),
      renderInputField("isbn13", isbn13Var, "ISBN (13-значный)"),
      renderInputField("edition", editionVar, "Издание"),
      renderInputField("year", yearVar, "Год издания"),
      renderInputField("pages", pagesVar, "Количество страниц"),
      renderInputField("description", descriptionVar, "Описание"),
      renderInputField("language", languageVar, "Язык издания"),
      renderInputField("category", categoryVar, "Категория")
    )

  private def renderInputField(
      fieldId: String,
      fieldVar: Var[String],
      description: String
  ): HtmlElement =
    div(
      label(
        forId  := fieldId,
        description
      ),
      input(
        cls    := "input input-create-book",
        idAttr := fieldId,
        onInput.mapToValue --> fieldVar
      )
    )

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
              val jwt = dom.window.localStorage.getItem("jwt")
              EventStream.fromFuture(httpClient.post[Book, Book](url, jwt, book))
            case Failure(exception) =>
              EventStream.fromFuture(Future.successful(Left(exception)))
          } --> {
          case Left(err: Throwable) => dom.window.alert(err.toString)
          case Right(value)         => router.pushState(BooksPage())
        }
      )
    )

end CreateBookView
