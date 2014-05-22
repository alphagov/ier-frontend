package uk.gov.gds.ier.form

import org.scalatest.FlatSpec
import org.scalatest.Matchers
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.transaction.ordinary.confirmation.ConfirmationForms
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.service.LocateService
import org.scalatest.mock.MockitoSugar
import org.mockito.Mockito._
import uk.gov.gds.ier.service.WithAddressService
import uk.gov.gds.ier.transaction.ordinary.nationality.NationalityForms

class OrdinaryFormImplicitsTests
  extends FlatSpec
  with Matchers
  with FormKeys
  with TestHelpers
  with OrdinaryFormImplicits
  with MockitoSugar
  with WithAddressService
  with NationalityForms {

  val mockPlaces = mock[LocateService]
  val addressService = new AddressService(mockPlaces)
  val serialiser = jsonSerialiser

  behavior of "OrdinaryFormImplicits"
  it should "generate errors if non valid country is provided" in {
    val value = Map(
        "nationality.british" -> "true",
        "nationality.irish" -> "true",
        "nationality.hasOtherCountry" -> "true",
        "nationality.otherCountries[0]" -> "country 1")

    nationalityForm.bind(value).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map("nationality.otherCountries[0]" -> Seq("ordinary_nationality_not_valid")))
      },
      success => fail("Should have thrown an error")
    )
  }

    it should "generate errors if more than " in {
    val value = Map(
        "nationality.hasOtherCountry" -> "true",
        "nationality.otherCountries[0]" -> "China",
        "nationality.otherCountries[1]" -> "Canada",
        "nationality.otherCountries[2]" -> "Australia",
        "nationality.otherCountries[3]" -> "New Zealand",
        "nationality.otherCountries[4]" -> "France",
        "nationality.otherCountries[5]" -> "Spain")

    nationalityForm.bind(value).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.errorMessages("nationality") should be(Seq("ordinary_nationality_no_more_five_countries"))
        hasErrors.globalErrorMessages should be(Seq("ordinary_nationality_no_more_five_countries"))
      },
      success => fail("Should have thrown an error")
    )
  }
  it should "bind successfully if less than 6 other countries provided are valid" in {
    val value = Map(
        "nationality.british" -> "true",
        "nationality.irish" -> "true",
        "nationality.hasOtherCountry" -> "true",
        "nationality.otherCountries[0]" -> "China",
        "nationality.otherCountries[1]" -> "Canada",
        "nationality.otherCountries[2]" -> "Australia")

    nationalityForm.bind(value).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        val otherCountries = success.nationality.map(_.otherCountries).getOrElse(List(Nil))
        otherCountries.size should be(3)
        otherCountries(0) should be ("China")
        otherCountries(1) should be ("Canada")
        otherCountries(2) should be ("Australia")
      }
    )
  }
}