package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.Pages.*
import libra.Routes.*
import libra.entities.User
import libra.http.HttpClient
import org.scalajs.dom

import scala.concurrent.*

/** Представление страницы регистрации. */
class RegistrationView(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val url        = libra.App.apiPath + "/registration"

  /** Рендер страницы регистрации. */
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

  private val nameVar: Var[String] = Var("")

  /** Рендер поля ввода имени пользователя. */
  private def renderNameInput: HtmlElement =
    div(
      input(
        cls := "input input-login",
        placeholder("Имя"),
        onInput.mapToValue --> nameVar
      )
    )

  private val emailVar: Var[String] = Var("")

  /** Рендер поля ввода электронной почты. */
  private def renderEmailInput: HtmlElement =
    div(
      input(
        cls := "input input-login",
        typ := "email",
        placeholder("Электронная почта"),
        onInput.mapToValue --> emailVar
      )
    )

  private val passwordVar: Var[String] = Var("")

  /** Рендер поля ввода пароля. */
  private def renderPasswordInput: HtmlElement =
    div(
      input(
        cls := "input input-login",
        typ := "password",
        placeholder("Пароль"),
        onInput.mapToValue --> passwordVar
      )
    )

  /** Рендер кнопки регистрации. */
  private def renderRegisterButton: HtmlElement =
    div(
      button(
        cls := "button button-login",
        "Зарегистрироваться",
        onClick
          .mapTo(User(None, nameVar.now(), emailVar.now(), Some(passwordVar.now())))
          .flatMap(user =>
            if user.isValid then
              EventStream
                .fromFuture(httpClient.post[User, User](url, user))
            else
              EventStream
                .fromFuture(Future.successful(Left(Exception("Введите валидные данные"))))
          ) --> {
          case Left(err: Throwable)  => dom.window.alert(err.toString)
          case Right(response: User) => router.pushState(LoginPage())
        }
      )
    )

end RegistrationView
