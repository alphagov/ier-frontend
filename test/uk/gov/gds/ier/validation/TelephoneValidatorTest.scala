package uk.gov.gds.ier.validation

import uk.gov.gds.ier.test.UnitTestSuite

class TelephoneValidatorTest extends UnitTestSuite {

  it should "return true for a valid telephone number" in {
    TelephoneValidator.isValid("0123456789") should be(true)
  }

  it should "return true for a telephone number that would be a valid telephone number without white characters" in {
    TelephoneValidator.isValid("0 1 2 3 4 5 6 7 8 9") should be(true)
  }

  it should "return true for a telephone number with 20 plus digits" in {
    TelephoneValidator.isValid("0123456789012345678912") should be(true)
  }

  it should "return true for an international telephone number" in {
    TelephoneValidator.isValid("(+44)1234567890") should be(true)
  }

  it should "return true for an international telephone number with an extension" in {
    TelephoneValidator.isValid("(+44)1234567890ext123") should be(true)
  }

  it should "return true for a telephone number with symbols" in {
    TelephoneValidator.isValid("(+44)1234567890-123_") should be(true)
  }

  it should "return false for invalid telephone number" in {
    TelephoneValidator.isValid("abc12") should be(false)
  }

  it should "return false for telephone number of 2 digits" in {
    TelephoneValidator.isValid("12") should be(false)
  }

  it should "return false for telephone number of 31 digits" in {
    TelephoneValidator.isValid("1234567891234567890123456789012") should be(false)
  }

  it should "return false for telephone number with rogue symbols" in {
    TelephoneValidator.isValid("0123456789%$£") should be(false)
  }
}
