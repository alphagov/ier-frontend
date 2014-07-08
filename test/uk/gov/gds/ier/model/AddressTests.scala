package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.CustomMatchers
import org.scalatest.{Matchers, FlatSpec}

class AddressTests
  extends FlatSpec
  with Matchers
  with CustomMatchers {

  it should "generate the expected payload with lower-cased, no whitespaces postcode" in {
    Address(" aB1  2Cd ").toApiMap("reg") should matchMap(Map("regpostcode" -> "ab12cd"))
    Address("ab12cd").toApiMap("reg") should matchMap(Map("regpostcode" -> "ab12cd"))
    Address("  A  B  12  C  D  ").toApiMap("reg") should matchMap(Map("regpostcode" -> "ab12cd"))
  }

}
