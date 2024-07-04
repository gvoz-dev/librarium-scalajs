package libra.utils

import org.scalajs.dom

object Misc:

  /** Расширение для трансформации строк в [[Option]]:
    *   - если строка пуста - [[None]]
    *   - иначе - [[Some]], содержащий эту строку
    */
  extension (string: String)
    def transformToOption: Option[String] =
      if string.isEmpty then None
      else Some(string)

  /** Возвращает JWT из Local Storage. */
  def getJwt: Option[String] =
    val jwt = dom.window.localStorage.getItem("jwt")
    Option(jwt)

end Misc
