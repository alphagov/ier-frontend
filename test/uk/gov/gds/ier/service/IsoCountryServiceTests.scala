package uk.gov.gds.ier.service

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.Nationality

class IsoCountryServiceTests
  extends FlatSpec
  with Matchers
  with TestHelpers {

  it should "fill in iso codes from country names" in {
    val nationality = Nationality(british = Some(true), irish = Some(true), otherCountries = List("France", "Italy"))
    val outputNationality = new IsoCountryService().transformToIsoCode(nationality)

    val Some(isoCodes) = outputNationality.countryIsos

    outputNationality should be(nationality.copy(countryIsos = Some(isoCodes)))
    isoCodes should contain("GB")
    isoCodes should contain("IE")
    isoCodes should contain("FR")
    isoCodes should contain("IT")
  }

  it should "handle no iso codes instance" in {
    new IsoCountryService().transformToIsoCode(Nationality()).countryIsos should be(None)
  }

  it should "handle bad country names" in {
    new IsoCountryService().transformToIsoCode(Nationality(otherCountries = List("BLARGH"))).countryIsos should be(None)
  }
}
