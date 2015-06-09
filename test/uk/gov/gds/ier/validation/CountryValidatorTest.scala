package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.Country
import uk.gov.gds.ier.test.UnitTestSuite

class CountryValidatorTest extends UnitTestSuite {

  behavior of "CountryValidator.isScotland"
  it should "return true for Scotland" in {
    CountryValidator.isScotland(Some(Country("Scotland",false))) should be(true)
  }

  it should "return false for Non-Scotland" in {
    CountryValidator.isScotland(Some(Country("England",false))) should be(false)
  }
}
