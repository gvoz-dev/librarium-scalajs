package libra.views

import com.raquo.laminar.api.L.{*, given}

/** Представление страницы "404 Not Found" */
class NotFoundView extends View:

  /** Рендер страницы "404 Not Found" */
  override def render: HtmlElement =
    div(
      h1("404 Not Found")
    )

end NotFoundView
