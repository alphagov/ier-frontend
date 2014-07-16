package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.CustomMatchers
import org.scalatest.{Matchers, FlatSpec}

class AddressTests
  extends FlatSpec
  with Matchers
  with CustomMatchers {

  it should "generate the expected payload with correct format postcode" in {
    Address(" aB1  2Cd ").toApiMap("reg") should matchMap(Map("regpostcode" -> "AB1 2CD"))
    Address("ab12cd").toApiMap("reg") should matchMap(Map("regpostcode" -> "AB1 2CD"))
    Address("  A  B  12  C  D  ").toApiMap("reg") should matchMap(Map("regpostcode" -> "AB1 2CD"))
  }

}
