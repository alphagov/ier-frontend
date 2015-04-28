package uk.gov.gds.ier.validation

import uk.gov.gds.ier.test.UnitTestSuite

class PassportNumberValidatorTest extends UnitTestSuite {

  it should "return true for a valid passport number" in {
    PassportNumberValidator.isValid("123456789") should be(true)
  }

  it should "return true for a passport number that would be a valid passport number without white characters" in {
    PassportNumberValidator.isValid("1 2 3 4 5 6 7  8   9") should be(true)
  }

  it should "return false for invalid passport number" in {
    PassportNumberValidator.isValid("abcdefghi") should be(false)
  }

  it should "return false for invalid passport number with less than 9 characters" in {
    PassportNumberValidator.isValid("12345678") should be(false)
  }

  it should "return false for empty string" in {
    PassportNumberValidator.isValid("") should be(false)
  }

  it should "return false for passport number with hyphen (-)" in {
    PassportNumberValidator.isValid("123-456-789") should be(false)
  }
}
