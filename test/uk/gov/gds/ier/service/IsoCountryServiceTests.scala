package uk.gov.gds.ier.service

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.PartialNationality

class IsoCountryServiceTests
  extends FlatSpec
  with Matchers
  with TestHelpers {

  it should "fill in iso codes from country names" in {
    val nationality = PartialNationality(british = Some(true), irish = Some(true), otherCountries = List("France", "Italy"))
    val isoNationality = new IsoCountryService().transformToIsoCode(nationality)

    isoNationality.countryIsos should contain("GB")
    isoNationality.countryIsos should contain("IE")
    isoNationality.countryIsos should contain("FR")
    isoNationality.countryIsos should contain("IT")
  }

  it should "handle no iso codes instance" in {
    new IsoCountryService().transformToIsoCode(PartialNationality()).countryIsos should be(Nil)
  }

  it should "handle bad country names" in {
    new IsoCountryService().transformToIsoCode(PartialNationality(otherCountries = List("BLARGH"))).countryIsos should be(Nil)
  }
}
