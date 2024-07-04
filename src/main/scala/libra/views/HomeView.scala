package libra.views

import com.raquo.laminar.api.L.{*, given}
import libra.Pages.*
import libra.Routes.*

/** Представление главной страницы. */
class HomeView extends View:

  /** Рендер главной страницы. */
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

  /** Рендер кнопки входа - переход на страницу аутентификации. */
  private def renderLoginButton: HtmlElement =
    import com.raquo.laminar.api.features.unitArrows
    div(
      button(
        cls := "button button-login",
        "Вход",
        onClick --> { router.pushState(LoginPage()) }
      )
    )

  /** Рендер кнопки регистрации - переход на соответствующую страницу. */
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
