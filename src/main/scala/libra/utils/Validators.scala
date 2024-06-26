package libra.utils

object Validators:

  private val emailRegex = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}".r

  /** Валидация введённого пользователем адреса электронной почты.
    *
    * @param email
    *   адрес электронной почты
    */
  def validateEmail(email: String): Boolean =
    emailRegex.findFirstMatchIn(email).isDefined

end Validators
