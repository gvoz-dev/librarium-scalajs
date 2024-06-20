package libra.views

import com.raquo.laminar.api.L.{*, given}

case class NotFoundView() extends View:

  override def render: HtmlElement =
    div(
      h1("404 Not Found")
    )

end NotFoundView
