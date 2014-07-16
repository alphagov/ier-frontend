package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import play.api.libs.json.{Json, JsNull}

class LocalAuthorityFormTests
  extends FlatSpec
  with Matchers
  with LocalAuthorityLookupForm
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WithSerialiser{

  val serialiser = jsonSerialiser

  behavior of "LocalAuthorityLookupForm.localAuthorityLookupForm"
  it should "successfully bind a valid postcode" in {
    val js = Json.toJson(
      Map("postcode" -> "SW1A1AA")
    )
    localAuthorityLookupForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors)),
      success => {
        success.postcode should be("SW1A1AA")
      }
    )
  }

  it should "error out on empty json" in {
    val js = JsNull

    localAuthorityLookupForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_address_error_pleaseEnterYourPostcode"))
        hasErrors.errorMessages("postcode") should be(Seq("ordinary_address_error_pleaseEnterYourPostcode"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on no values" in {
    val js = Json.toJson(Map.empty[String, String])

    localAuthorityLookupForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_address_error_pleaseEnterYourPostcode"))
        hasErrors.errorMessages("postcode") should be(Seq("ordinary_address_error_pleaseEnterYourPostcode"))
      },
      success => fail("Should have failed out")
    )
  }

  it should "error out on empty values" in {
    val js =  Json.toJson(
      Map("postcode" -> "")
    )
    localAuthorityLookupForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_address_error_pleaseEnterYourPostcode"))
        hasErrors.errorMessages("postcode") should be(Seq("ordinary_address_error_pleaseEnterYourPostcode"))
      },
      success => fail("Should have errored out")
    )
  }

  it should "error out on invalid postcode" in {
    val js = Json.toJson(
      Map("postcode" -> "1Nv4|_1D")
    )
    localAuthorityLookupForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_address_error_postcodeIsNotValid"))
        hasErrors.errorMessages("postcode") should be(Seq("ordinary_address_error_postcodeIsNotValid"))
      },
      success => fail("Should have failed out")
    )
  }

}
