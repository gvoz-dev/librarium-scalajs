package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.Pages.*
import libra.Routes.*
import libra.entities.{*, given}
import libra.http.HttpClient
import org.scalajs.dom

import scala.concurrent.*

/** Представление страницы аутентификации. */
class LoginView(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val url        = libra.App.apiPath + "/login"

  /** Рендер страницы аутентификации. */
  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        cls := "block-login",
        h1("Librarium"),
        h2("Вход"),
        renderEmailInput,
        renderPasswordInput,
        renderLoginButton
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

  /** Рендер кнопки входа. */
  private def renderLoginButton: HtmlElement =
    div(
      button(
        cls := "button button-login",
        "Войти",
        onClick
          .mapTo(Credentials(emailVar.now(), passwordVar.now()))
          .flatMap(credentials =>
            if credentials.isValid then
              EventStream
                .fromFuture(httpClient.post[Credentials, Token](url, credentials))
            else
              EventStream
                .fromFuture(Future.successful(Left(Exception("Введите валидные адрес электронной почты и пароль"))))
          ) --> {
          case Left(err: Throwable)   => dom.window.alert(err.toString)
          case Right(response: Token) =>
            dom.window.localStorage.setItem("jwt", response.jwt)
            router.pushState(BooksPage())
        }
      )
    )

end LoginView
