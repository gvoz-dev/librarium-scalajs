package libra.utils

import libra.utils.Validators.validateEmail
import org.scalatest.funsuite.AnyFunSuiteLike

class ValidatorsTest extends AnyFunSuiteLike:

  test("#validateEmail is correct") {
    assert(!validateEmail("test"))
    assert(!validateEmail("test@"))
    assert(!validateEmail("@example"))
    assert(!validateEmail("@example.com"))
    assert(!validateEmail("test@example"))
    assert(!validateEmail("test@example.c"))
    assert(validateEmail("test@example.com"))
  }

end ValidatorsTest
