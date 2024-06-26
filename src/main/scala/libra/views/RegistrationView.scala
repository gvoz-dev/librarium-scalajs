package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.Pages.*
import libra.Routes.*
import libra.entities.{*, given}
import libra.http.HttpClient
import org.scalajs.dom

import scala.concurrent.*

case class RegistrationView()(using ExecutionContext) extends View:

  private val httpClient               = HttpClient()
  private val url                      = libra.App.apiPath + "/registration"
  private val nameVar: Var[String]     = Var("")
  private val emailVar: Var[String]    = Var("")
  private val passwordVar: Var[String] = Var("")

  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        cls := "block-login",
        h1("Librarium"),
        h2("Регистрация"),
        renderNameInput,
        renderEmailInput,
        renderPasswordInput,
        renderRegisterButton
      )
    )

  private def renderNameInput: HtmlElement =
    div(
      input(
        cls := "input input-login",
        placeholder("Имя"),
        onInput.mapToValue --> nameVar
      )
    )

  private def renderEmailInput: HtmlElement =
    div(
      input(
        cls := "input input-login",
        placeholder("Электронная почта"),
        onInput.mapToValue --> emailVar
      )
    )

  private def renderPasswordInput: HtmlElement =
    div(
      input(
        cls := "input input-login",
        typ := "password",
        placeholder("Пароль"),
        onInput.mapToValue --> passwordVar
      )
    )

  private def renderRegisterButton: HtmlElement =
    div(
      button(
        cls := "button button-login",
        "Зарегистрироваться",
        onClick
          .mapTo(User(None, nameVar.now(), emailVar.now(), Some(passwordVar.now())))
          .flatMap(user =>
            if user.isValid then EventStream.fromFuture(httpClient.post[User, User](url, user))
            else
              EventStream.fromFuture(
                Future.successful(Left(Exception("Введите валидный адрес электронной почты и пароль")))
              )
          ) --> {
          case Left(err: Throwable) => dom.window.alert(err.getMessage)
          case Right(response)      => router.pushState(LoginPage())
        }
      )
    )

end RegistrationView
