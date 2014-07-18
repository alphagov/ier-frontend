package uk.gov.gds.ier.validation

import org.scalatest.{Matchers, FlatSpec}

class EmailValidatorTest
  extends FlatSpec
  with Matchers {

  behavior of "EmailValidator.isValid"

  it must "have an @ in it, at approx the right place" in {
    EmailValidator.isValid("1@dd.com") should be(true)
    EmailValidator.isValid("1@.com") should be(false)
  }

  it must "not have 2 dots prior to any subdomain" in {
    EmailValidator.isValid("1@d..d.com") should be(false)
    EmailValidator.isValid("1@d..d..com") should be(false)
  }

  it must "have something prior to the @" in {
    EmailValidator.isValid("1@1.com") should be(true)
    EmailValidator.isValid("@1.com") should be(false)
  }

  it should "accept regular valid email addresses" in {
    EmailValidator.isValid("regular@email.com") should be(true)
  }

  it should "accept _.' and % in user name of email address" in {
    EmailValidator.isValid("with_all_acceptable_chars_._%+'-@email.co.uk") should be(true)
  }

  it should "accept _.' and % in domain name of email address" in {
    EmailValidator.isValid("regular@email._%+'-.co.uk") should be(true)
  }

  it should "accept single quote (apostrophe) in user name" in {
    EmailValidator.isValid("connor.o'brien@thomond.ie") should be(true)
  }

  it should "accept single quote (apostrophe) in domain name" in {
    EmailValidator.isValid("connor@o'briens.ie") should be(true)
  }

  it should "accept real apostrophe in user name" in {
    EmailValidator.isValid("connor.o’brien@thomond.ie") should be(true)
  }

  it should "accept real apostrophe in server name" in {
    EmailValidator.isValid("connor@o’briens.ie") should be(true)
  }

  it should "accept spaces" in {
    EmailValidator.isValid("regular joe@email.com") should be(true)
    EmailValidator.isValid("regular.joe@somewhere on net.com") should be(true)
  }

  it should "reject email address with missing TLD" in {
    EmailValidator.isValid("invalidstructure@foo") should be(false)
  }

  it should "accept email address with TLD shorter than 2 characters" in {
    EmailValidator.isValid("invalidstructure@foo.c") should be(true)
  }

  it should "accept email address with a sub domain shorter than 2 characters" in {
    EmailValidator.isValid("invalidstructure@foo.c.com") should be(true)
  }

  it should "accept email with special characters like ’&^/ in user name" in {
    EmailValidator.isValid("invalid’&^/chars@email.com") should be(true)
  }

  it should "reject email address with no @" in {
      EmailValidator.isValid("no-at-sign.com") should be(false)
  }

  it should "accept email address with multiple @ (ie domain name containing @)" in {
    EmailValidator.isValid("tooM_many@at@chars.com") should be(true)
  }

  it should "reject multiple consecutive dots in domain name" in {
    EmailValidator.isValid("double.dot@foo..fooo.com") should be(false)
    EmailValidator.isValid("double.dot@foo.fooo....com") should be(false)
  }

  it should "accept multiple consecutive dots in user name" in {
    EmailValidator.isValid("double..dot@foo.fooo.com") should be(true)
  }

  it should "accept new top level domains" in {
    EmailValidator.isValid("jan@something.london") should be(true)
    EmailValidator.isValid("jan@something.photography") should be(true)
    EmailValidator.isValid("jan@something.computer") should be(true)
    EmailValidator.isValid("jan@something.coffee") should be(true)
    EmailValidator.isValid("jan@something.technology") should be(true)
    EmailValidator.isValid("jan@something.properties") should be(true)
  }

  it should "accept hot new top level domains" in {
    EmailValidator.isValid("jan@something.accountant") should be(true)
    EmailValidator.isValid("jan@something.limited") should be(true)
    EmailValidator.isValid("jan@something.management") should be(true)
    EmailValidator.isValid("jan@something.app") should be(true)
    EmailValidator.isValid("jan@something.blog") should be(true)
    EmailValidator.isValid("jan@something.ltd") should be(true)
    EmailValidator.isValid("jan@something.discount") should be(true)
    EmailValidator.isValid("jan@something.consulting") should be(true)
  }

  it should "reject address with just top level domain name" in {
    EmailValidator.isValid("jan@com") should be(false)
  }

  it should "accept email address with digits in TLD" in {
    EmailValidator.isValid("not.so.nice.guy@hell.666") should be(true)
    EmailValidator.isValid("winner@always.1st") should be(true)
    EmailValidator.isValid("mathematician@numeric.1") should be(true)
  }

  it should "accept email address with digits in subdomains" in {
    EmailValidator.isValid("admin.2@1and1.co.uk") should be(true)
    EmailValidator.isValid("22.ad22min.2222@11and1.22.uk") should be(true)
  }
}
