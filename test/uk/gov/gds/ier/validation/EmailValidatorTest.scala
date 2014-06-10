package uk.gov.gds.ier.validation

import org.scalatest.{Matchers, FlatSpec}

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class EmailValidatorTest
  extends FlatSpec 
  with Matchers {

  behavior of "EmailValidator.isValid"
  it should "accept valid email addresses" in {
    EmailValidator.isValid("regular@email.com") should be(true)
    EmailValidator.isValid("with_all_acceptable_chars_._%+'-@email._%+'-.co.uk") should be(true)
  }

  it should "reject invalid email addresses" in {
    EmailValidator.isValid("just invalid") should be(false)
    EmailValidator.isValid("invalid spaces@email.com") should be(false)
    EmailValidator.isValid("invalidstructure@emam") should be(false)
    EmailValidator.isValid("invalidstructure@emam.c") should be(false)
    EmailValidator.isValid("invalid’&^/chars@email.com") should be(false)
    EmailValidator.isValid("tooM_many@at@chars.com") should be(false)
  }

}
