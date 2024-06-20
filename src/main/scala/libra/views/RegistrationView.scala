package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.App.given
import libra.backendApiUrl
import libra.entities.User
import libra.http.{*, given}
import org.scalajs.dom

import scala.concurrent.*

case class RegistrationView() extends View:

  private val httpClient = HttpClient()
  private val url = backendApiUrl + "/registration"
  private val nameVar: Var[String] = Var("")
  private val emailVar: Var[String] = Var("")
  private val passwordVar: Var[String] = Var("")

  /** Рендер страницы входа в приложение. */
  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        cls := "block-user",
        h1("Librarium"),
        h2("Регистрация"),
        renderNameInput,
        renderEmailInput,
        renderPasswordInput,
        renderEnterButton
      )
    )

  private def renderNameInput: HtmlElement =
    div(
      input(
        cls := "input input-user",
        placeholder("Имя"),
        onInput.mapToValue --> nameVar
      )
    )

  private def renderEmailInput: HtmlElement =
    div(
      input(
        cls := "input input-user",
        placeholder("Электронная почта"),
        onInput.mapToValue --> emailVar
      )
    )

  private def renderPasswordInput: HtmlElement =
    div(
      input(
        cls := "input input-user",
        typ := "password",
        placeholder("Пароль"),
        onInput.mapToValue --> passwordVar
      )
    )

  private def renderEnterButton: HtmlElement =
    div(
      button(
        cls := "button button-user",
        typ := "submit",
        "Зарегистрироваться",
        onClick
          .mapTo(
            User(None, nameVar.now(), emailVar.now(), Some(passwordVar.now()))
          )
          .flatMap(user =>
            if true then
              EventStream.fromFuture(
                httpClient.post[User, User](url, user)
              )
            else
              EventStream.fromFuture(
                Future.successful(
                  Left(
                    Exception(
                      "Введите валидный адрес электронной почты и пароль"
                    )
                  )
                )
              )
          ) --> {
          case Left(err: Throwable) => dom.window.alert(err.getMessage)
          case Right(response)      => dom.window.alert(response.toString)
        }
      )
    )

end RegistrationView
