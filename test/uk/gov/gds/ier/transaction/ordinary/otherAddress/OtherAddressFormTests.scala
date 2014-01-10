package uk.gov.gds.ier.transaction.otherAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.test.TestHelpers
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
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("otherAddress") should be(Seq("Please answer this question"))
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
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errorMessages("otherAddress") should be(Seq("Please answer this question"))
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
        hasErrors.globalErrorMessages should be(Seq("error.boolean"))
        hasErrors.errorMessages("otherAddress.hasOtherAddress") should be(Seq("error.boolean"))
      },
      success => fail("Should have thrown an error")
    )
  }

  it should "successfully bind (true)" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "true"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.otherAddress.isDefined should be(true)
        val otherAddress = success.otherAddress.get
        otherAddress.hasOtherAddress should be(true)
      }
    )
  }

  it should "successfully bind(false)" in {
    val js = Json.toJson(
      Map(
        "otherAddress.hasOtherAddress" -> "false"
      )
    )
    otherAddressForm.bind(js).fold(
      hasErrors => fail(serialiser.toJson(hasErrors.prettyPrint)),
      success => {
        success.otherAddress.isDefined should be(true)
        val otherAddress = success.otherAddress.get
        otherAddress.hasOtherAddress should be(false)
      }
    )
  }
}
