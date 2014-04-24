package uk.gov.gds.ier.transaction.ordinary.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.libs.json.{JsNull, Json}
import uk.gov.gds.ier.model.{Addresses, PartialAddress, MovedHouseOption}
import uk.gov.gds.ier.transaction.ordinary.address.AddressForms

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
    assertUnsuccessfulBinding(
      formData = Map.empty,
      expectedErrorMessage = "Please answer this question")
  }

  it should "error out on missing values in input" in {
    assertUnsuccessfulBinding(
      formData = Map("previousAddress.movedRecently" -> ""),
      expectedErrorMessage = "Please answer this question")
  }

  it should "error out when moved from abroad selected without answering registered yes/no question" in {
    assertUnsuccessfulBinding(
      formData = Map("previousAddress.movedRecently" -> "from-abroad"),
      expectedErrorMessage = "Please answer this question")
  }

  it should "successfully bind when user has previous address (from uk)" in {
    assertSuccessfullBinding(
      formData = Map("previousAddress.movedRecently.movedRecently" -> "from-uk"),
      expected = MovedHouseOption.MovedFromUk)
  }

  it should "successfully bind when user has previous address (from abroad and registered)" in {
    assertSuccessfullBinding(
      formData = Map(
        "previousAddress.movedRecently.movedRecently" -> "from-abroad",
        "previousAddress.movedRecently.wasRegisteredWhenAbroad" -> "true"),
      expected = MovedHouseOption.MovedFromAbroadRegistered)
  }

  it should "successfully bind when user has previous address (from abroad and not registered)" in {
    assertSuccessfullBinding(
      formData = Map(
        "previousAddress.movedRecently.movedRecently" -> "from-abroad",
        "previousAddress.movedRecently.wasRegisteredWhenAbroad" -> "false"),
      expected = MovedHouseOption.MovedFromAbroadNotRegistered)
  }

  it should "successfully bind when user does not has previous address" in {
    assertSuccessfullBinding(
      formData = Map("previousAddress.movedRecently.movedRecently" -> "no"),
      expected = MovedHouseOption.NotMoved)
  }

  def assertSuccessfullBinding(formData:Map[String,String], expected:MovedHouseOption) {
    previousAddressFirstForm.bind(Json.toJson(formData)).fold(
      hasErrors => {
        fail("Binding failed with " + hasErrors.errorsAsTextAll)
      },
      success => {
        success.previousAddress.flatMap(_.movedRecently) should be(Some(expected))
      }
    )
  }

  def assertUnsuccessfulBinding(formData:Map[String,String], expectedErrorMessage:String) {
    val js = if(formData.isEmpty) JsNull else Json.toJson(formData)

    previousAddressFirstForm.bind(js).fold(
      hasErrors => {
        hasErrors.keyedErrorsAsMap should matchMap(Map(
          "previousAddress.movedRecently" -> Seq(expectedErrorMessage)
        ))
      },
      success => fail("Should have errored out.")
    )
  }
}
