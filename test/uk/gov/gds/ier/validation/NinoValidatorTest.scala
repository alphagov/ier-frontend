package uk.gov.gds.ier.validation

import org.specs2.mutable.Specification

class NinoValidatorTest extends Specification {

  "isValid" should {
    "return true for a valid NINO with suffix" in {
      NinoValidator.isValid("AB123456C") mustEqual(true)
    }
  }

  "isValid" should {
    "return true for a valid NINO without suffix" in {
      NinoValidator.isValid("AB123456") mustEqual(true)
    }
  }

  "isValid" should {
    "return false for an empty NINO" in {
      NinoValidator.isValid("") mustEqual(false)
    }
  }

  "isValid" should {
    "return true for a valid NINO with spaces" in {
      NinoValidator.isValid(" AB 12 3 456 C ") mustEqual(true)
    }
  }

  "isValid" should {
    "be case-insensitive" in {
      NinoValidator.isValid("Ab123456c") mustEqual(true)
    }
  }

  "isValid" should {
    "return false for an invalid NINO" in {
      NinoValidator.isValid("AB123456X") mustEqual(false)
      NinoValidator.isValid("AB123456Cx") mustEqual(false)
      NinoValidator.isValid("AB12345") mustEqual(false)
    }
  }
}
