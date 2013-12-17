package uk.gov.gds.ier.service

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.Nationality

class IsoCountryServiceTests
  extends FlatSpec
  with Matchers
  with TestHelpers {

  behavior of "IsoCountryService.transformToIsoCode"
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

  behavior of "IsoCountryService.getFranchises"
  it should "provide the correct franchises for the checked countries" in {
    val service = new IsoCountryService()

    service.getFranchises(Nationality(british = Some(true))) should equal(List("Full", "EU", "Commonwealth"))
    service.getFranchises(Nationality(irish = Some(true))) should equal(List("Full", "EU"))
  }

  it should "provide only distinct franchises for multiple countries" in {
    val service = new IsoCountryService()

    service.getFranchises(Nationality(british = Some(true), irish = Some(true))) should equal(List("Full", "EU", "Commonwealth"))
    service.getFranchises(Nationality(british = Some(true), otherCountries = List("France"))) should equal(List("Full", "EU", "Commonwealth"))
    service.getFranchises(Nationality(otherCountries = List("Japan", "France"))) should equal(List("EU"))
  }

  it should "not provide a franchise for unfranchised countries" in {
    val service = new IsoCountryService()

    service.getFranchises(Nationality(otherCountries = List("Japan"))) should equal(List.empty)
    service.getFranchises(Nationality(otherCountries = List("Afghanistan"))) should equal(List.empty)
  }
}
