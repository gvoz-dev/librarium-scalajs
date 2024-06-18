package libra.views

import com.raquo.laminar.api.L.HtmlElement

/** Компонент представления. */
trait View:

  /** Рендер компонента представления. */
  def render: HtmlElement

end View
