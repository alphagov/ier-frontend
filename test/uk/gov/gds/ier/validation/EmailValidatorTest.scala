package uk.gov.gds.ier.validation

import org.scalatest.{Matchers, FlatSpec}

class EmailValidatorTest
  extends FlatSpec
  with Matchers {

  behavior of "EmailValidator.isValid"

  it should "accept regular valid email addresses" in {
    EmailValidator.isValid("regular@email.com") should be(true)
  }

  it should "accept _.' and % in user name of email address" in {
    EmailValidator.isValid("with_all_acceptable_chars_._%+'-@email.co.uk") should be(true)
  }

  it should "reject _.' and % in domain name of email address" in {
    EmailValidator.isValid("regular@email._%+'-.co.uk") should be(false)
  }

  it should "accept single quote (apostrophe) in user name" in {
    EmailValidator.isValid("connor.o'brien@thomond.ie") should be(true)
  }

  it should "reject single quote (apostrophe) in domain name" in {
    EmailValidator.isValid("connor@o'briens.ie") should be(false)
  }

  it should "reject real apostrophe in user name" in {
    EmailValidator.isValid("connor.o’brien@thomond.ie") should be(false)
  }

  it should "reject real apostrophe in server name" in {
    EmailValidator.isValid("connor@o’briens.ie") should be(false)
  }

  it should "reject spaces" in {
    EmailValidator.isValid("regular joe@email.com") should be(false)
    EmailValidator.isValid("regular.joe@somewhere on net.com") should be(false)
    EmailValidator.isValid("just invalid") should be(false)
    EmailValidator.isValid("invalid spaces@email.com") should be(false)
  }

  it should "reject email address with missing TLD" in {
    EmailValidator.isValid("invalidstructure@foo") should be(false)
  }

  it should "reject email address with TLD shorter than 2 characters" in {
    EmailValidator.isValid("invalidstructure@foo.c") should be(false)
  }

  it should "accept email address with a sub domain shorter than 2 characters" in {
    EmailValidator.isValid("invalidstructure@foo.c.com") should be(true)
  }

  it should "reject email with special characters like ’&^/ in user name" in {
    EmailValidator.isValid("invalid’&^/chars@email.com") should be(false)
  }

  it should "reject email address with no @" in {
      EmailValidator.isValid("no-at-sign.com") should be(false)
  }

  it should "reject email address with too many @" in {
    EmailValidator.isValid("tooM_many@at@chars.com") should be(false)
  }

  it should "reject multiple consecutive dots in domain name" in {
    EmailValidator.isValid("double.dot@foo..fooo.com") should be(false)
    EmailValidator.isValid("double.dot@foo.fooo....com") should be(false)
  }

  it should "accept multiple consecutive dots in user name" in {
    EmailValidator.isValid("double..dot@foo.fooo.com") should be(true)
  }

  it should "accept new top level domains" in {
    // http://en.wikipedia.org/wiki/List_of_Internet_top-level_domains
    EmailValidator.isValid("jan@something.london") should be(true)
    EmailValidator.isValid("jan@something.photography") should be(true)
    EmailValidator.isValid("jan@something.computer") should be(true)
    EmailValidator.isValid("jan@something.coffee") should be(true)
    EmailValidator.isValid("jan@something.technology") should be(true)
    EmailValidator.isValid("jan@something.properties") should be(true)
  }

  it should "accept (?) hot new top level domains" in {
    // hot new, not yet approved (?) yet ofefred for sale on http://www.1and1.co.uk/new-top-level-domains as of 8/7/2014
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

  it should "reject (??) email address with digits in TLD" in {
    // not sure if this is right, but nearly all regex found on net does that and no TLD has a number so far
    EmailValidator.isValid("not.so.nice.guy@hell.666") should be(false)
    EmailValidator.isValid("winner@always.1st") should be(false)
    EmailValidator.isValid("mathematician@numeric.1") should be(false)
  }

  it should "accept email address with digits anywhere but TLD" in {
    EmailValidator.isValid("admin.2@1and1.co.uk") should be(true)
    EmailValidator.isValid("22.ad22min.2222@11and1.22.uk") should be(true)
  }
}
