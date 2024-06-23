package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.Pages.*
import libra.Routes.*

case class HomeView() extends View:

  override def render: HtmlElement =
    div(
      cls := "container",
      div(
        cls := "block-login",
        h1("Librarium"),
        renderLoginButton,
        renderRegisterButton
      )
    )

  private def renderLoginButton: HtmlElement =
    import com.raquo.laminar.api.features.unitArrows
    div(
      button(
        cls := "button button-login",
        "Вход",
        onClick --> { router.pushState(LoginPage()) }
      )
    )

  private def renderRegisterButton: HtmlElement =
    import com.raquo.laminar.api.features.unitArrows
    div(
      button(
        cls := "button button-login",
        "Регистрация",
        onClick --> { router.pushState(RegistrationPage()) }
      )
    )

end HomeView
