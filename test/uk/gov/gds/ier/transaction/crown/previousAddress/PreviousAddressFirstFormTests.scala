package uk.gov.gds.ier.transaction.crown.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.{JsNull, Json}
import uk.gov.gds.ier.transaction.crown.address.AddressForms
import uk.gov.gds.ier.model.MovedHouseOption

class PreviousAddressFirstFormTests
  extends FlatSpec
  with Matchers
  with AddressForms
  with PreviousAddressFirstForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty input" in {
    val js = JsNull
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values in input" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> ""
      )
    )
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("previousAddress.movedRecently") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "fail on invalid moved house value (from abroad)" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "from-abroad"
      )
    )
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.movedRecently" -> Seq("Not a valid option")
        ))
      },
      success => {
        fail("Should have errored out")
      }
    )
  }

  it should "fail on invalid moved house value (from uk)" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "from-uk"
      )
    )
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.movedRecently" -> Seq("Not a valid option")
        ))
      },
      success => {
        fail("Should have errored out")
      }
    )
  }

  it should "successfully bind when user has previous address" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "yes"
      )
    )
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        success.previousAddress.flatMap(_.movedRecently) should be(Some(MovedHouseOption.Yes))
      }
    )
  }

  it should "successfully bind when user does not has previous address" in {
    val js = Json.toJson(
      Map(
        "previousAddress.movedRecently" -> "no"
      )
    )
    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        success.previousAddress.flatMap(_.movedRecently) should be(Some(MovedHouseOption.NotMoved))
      }
    )
  }
}
