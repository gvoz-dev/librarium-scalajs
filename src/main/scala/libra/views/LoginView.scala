package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.backendApiUrl
import libra.http.{*, given}
import org.scalajs.dom

import scala.concurrent.*

/** Страница входа в приложение. */
class LoginView(using ExecutionContext) extends View:

  private val httpClient = HttpClient()
  private val loginApiUrl = backendApiUrl + "/login"
  private val emailVar: Var[String] = Var("")
  private val passwordVar: Var[String] = Var("")

  override def render: HtmlElement =
    div(
      h1(
        "Librarium"
      ),
      h2(
        "Вход"
      ),
      p(
        "Введите почту и пароль, чтобы войти"
      ),
      renderEmailInput,
      renderPasswordInput,
      renderEnterButton
    )

  private def renderEmailInput: HtmlElement =
    div(
      p(
        input(
          placeholder("Почта"),
          onInput.mapToValue --> emailVar
        )
      )
    )

  private def renderPasswordInput: HtmlElement =
    div(
      p(
        input(
          typ := "password",
          placeholder("Пароль"),
          onInput.mapToValue --> passwordVar
        )
      )
    )

  private def renderEnterButton: HtmlElement =
    div(
      p(
        button(
          typ := "submit",
          "Вход",
          onClick
            .mapTo(Credentials(emailVar.now(), passwordVar.now()))
            .flatMap(credentials =>
              if credentials.isValid then
                EventStream.fromFuture(
                  httpClient.post[Credentials, Token](loginApiUrl, credentials)
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
    )

end LoginView
