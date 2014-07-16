package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.{TestHelpers, CustomMatchers}
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.constants.NameConstants

class PostcodeTests
  extends FlatSpec
  with Matchers
  with CustomMatchers
  with TestHelpers {

  behavior of "Postcode.toApiFormat"
  it should "return empty string for empty postcode" in {
    Postcode.toApiFormat("") should be("")
  }

  it should "introduce insert space before last 3 characters" in {
    Postcode.toApiFormat("n") should be("N")
    Postcode.toApiFormat("un") should be("UN")
    Postcode.toApiFormat("6un") should be("6UN")
    Postcode.toApiFormat("n6un") should be("N 6UN")
    Postcode.toApiFormat("nw6un") should be("NW 6UN")
    Postcode.toApiFormat("nw26un") should be("NW2 6UN")
  }

  it should "strip all whitespaces" in {
    Postcode.toApiFormat("  n  w 26  u n  ") should be("NW2 6UN")
    Postcode.toApiFormat("   n W26UN   ") should be("NW2 6UN")
  }

  it should "format short and long postcodes correctly" in {
    Postcode.toApiFormat("s11aa") should be("S1 1AA")
    Postcode.toApiFormat("wc2b6se") should be("WC2B 6SE")
  }

  behavior of "Postcode.toDisplayFormat"
  it should "return empty string for empty postcode" in {
    Postcode.toDisplayFormat("") should be("")
  }

  it should "introduce insert space before last 3 characters" in {
    Postcode.toDisplayFormat("n") should be("N")
    Postcode.toDisplayFormat("un") should be("UN")
    Postcode.toDisplayFormat("6un") should be("6UN")
    Postcode.toDisplayFormat("n6un") should be("N 6UN")
    Postcode.toDisplayFormat("nw6un") should be("NW 6UN")
    Postcode.toDisplayFormat("nw26un") should be("NW2 6UN")
  }

  it should "strip all whitespaces" in {
    Postcode.toDisplayFormat("  n  w 26  u n  ") should be("NW2 6UN")
    Postcode.toDisplayFormat("   n W26UN   ") should be("NW2 6UN")
  }

  it should "format short and long postcodes correctly" in {
    Postcode.toDisplayFormat("s11aa") should be("S1 1AA")
    Postcode.toDisplayFormat("wc2b6se") should be("WC2B 6SE")
  }

}
