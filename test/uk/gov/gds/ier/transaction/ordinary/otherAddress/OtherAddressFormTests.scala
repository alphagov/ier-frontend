package uk.gov.gds.ier.transaction.ordinary.otherAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.OtherAddress
import play.api.libs.json.{Json, JsNull}

class OtherAddressFormTests
  extends FlatSpec
  with Matchers
  with OtherAddressForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty json" in {
    val js = JsNull

    otherAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_otheraddr_error_pleaseAnswer"))
        hasErrors.errorMessages("otherAddress") should be(Seq("ordinary_otheraddr_error_pleaseAnswer"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on empty values" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> ""
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("ordinary_otheraddr_error_pleaseAnswer"))
        hasErrors.errorMessages("otherAddress") should be(Seq("ordinary_otheraddr_error_pleaseAnswer"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "error out on invalid values" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "bleurch"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => {
        hasErrors.errors.size should be(2)
        hasErrors.globalErrorMessages should be(Seq("bleurch not a valid other address type"))
        hasErrors.errorMessages("otherAddress.hasOtherAddress") should be(
          Seq("bleurch not a valid other address type"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "successfully bind (student)" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "student"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.otherAddress.isDefined should be(true)
        val otherAddress = success.otherAddress.get
        otherAddress.otherAddressOption should be(OtherAddress.StudentOtherAddress)
      }
    )
  }

  it should "successfully bind (second home)" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "secondHome"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.otherAddress.isDefined should be(true)
        val otherAddress = success.otherAddress.get
        otherAddress.otherAddressOption should be(OtherAddress.HomeOtherAddress)
      }
    )
  }

  it should "successfully bind(false)" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "none"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.otherAddress.isDefined should be(true)
        val otherAddress = success.otherAddress.get
        otherAddress.otherAddressOption should be(OtherAddress.NoOtherAddress)
      }
    )
  }
}
