package uk.gov.gds.ier.model

import uk.gov.gds.ier.test.CustomMatchers
import org.scalatest.{Matchers, FlatSpec}

class PossibleContactAddressesTests
  extends FlatSpec
  with Matchers
  with CustomMatchers {

  it should "generate the expected payload with postcode in the correct format" in {
    val possibleAddress = PossibleContactAddresses(
      contactAddressType = Some("uk"),
      ukAddressLine = None,
      bfpoContactAddress = None,
      otherContactAddress = None
    )

    val possibleAddressMap = possibleAddress.toApiMap(Some(Address(" aB1  2Cd "))).asInstanceOf[Map[String, String]]
    possibleAddressMap should matchMap(Map("corrcountry" -> "uk", "corrpostcode" -> "AB1 2CD"))
  }

}
