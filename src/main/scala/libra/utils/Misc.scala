package libra.utils

object Misc:

  /** Расширение для трансформации строк в [[Option]]:
    *   - если строка пуста - [[None]]
    *   - иначе - [[Some]], содержащий эту строку
    */
  extension (string: String)
    def transformToOption: Option[String] =
      if string.isEmpty then None
      else Some(string)

end Misc
