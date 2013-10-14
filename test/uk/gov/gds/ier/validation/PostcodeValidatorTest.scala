package uk.gov.gds.ier.validation

import org.specs2.mutable._

class PostcodeValidatorTest extends Specification {

  "isValid" should {
    "return true for a valid postcode" in {
      PostcodeValidator.isValid("SW11 2DR") mustEqual true
    }
  }

  "isValid" should {
    "return true for a postcode that would be a valid postcode without white characters" in {
      PostcodeValidator.isValid(" S W 1 1 2D R    ") mustEqual true
    }
  }

  "isValid" should {
    "return true for a lowercase postcode" in {
      PostcodeValidator.isValid("sw11 2dr") mustEqual true
    }
  }

  "isValid" should {
    "return false for invalid postcode" in {
      PostcodeValidator.isValid("SW11X2DR") mustEqual false
    }
  }

  "isValid" should {
    "return false for empty string" in {
      PostcodeValidator.isValid("") mustEqual false
    }
  }
}
