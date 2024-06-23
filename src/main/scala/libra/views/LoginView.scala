package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.entities.{*, given}
import libra.http.HttpClient
import org.scalajs.dom

import scala.concurrent.*

case class LoginView()(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val url = libra.App.apiPath + "/login"
  private val emailVar: Var[String] = Var("")
  private val passwordVar: Var[String] = Var("")

  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        cls := "block-user",
        h1("Librarium"),
        h2("Вход"),
        renderEmailInput,
        renderPasswordInput,
        renderLoginButton
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

  private def renderLoginButton: HtmlElement =
    div(
      button(
        cls := "button button-user",
        typ := "submit",
        "Вход",
        onClick
          .mapTo(Credentials(emailVar.now(), passwordVar.now()))
          .flatMap(credentials =>
            if credentials.isValid then
              EventStream.fromFuture(
                httpClient.post[Credentials, Token](url, credentials)
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
          case Right(response: Token) =>
            dom.window.localStorage.setItem("jwt", response.jwt)
            dom.window.alert(dom.window.localStorage.getItem("jwt"))
        }
      )
    )

end LoginView
