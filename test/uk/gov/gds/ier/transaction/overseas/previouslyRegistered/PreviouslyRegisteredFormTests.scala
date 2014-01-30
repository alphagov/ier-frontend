package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.{Json, JsNull}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class PreviouslyRegisteredFormTests
  extends FlatSpec
  with Matchers
  with PreviouslyRegisteredForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty json" in {
    val js = JsNull
    previouslyRegisteredForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("previouslyRegistered.hasPreviouslyRegistered") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on missing values" in {
    val js = Json.toJson(
      Map(
        "previouslyRegistered.hasPreviouslyRegistered" -> ""
      )
    )
    previouslyRegisteredForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("previouslyRegistered.hasPreviouslyRegistered") should be(Seq("Please answer this question"))
        hasErrors.globalErrorMessages should be(Seq("Please answer this question"))
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "successfully parse (true)" in {
    val js = Json.toJson(
      Map(
        "previouslyRegistered.hasPreviouslyRegistered" -> "true"
      )
    )
    previouslyRegisteredForm.bind(js).fold(
      hasErrors => {
        fail("Should have errored out.")
      },
      success => {
        val Some(prevReg) = success.previouslyRegistered
        prevReg.hasPreviouslyRegistered should be(true)
      }
    )
  }

  it should "successfully parse (false)" in {
    val js = Json.toJson(
      Map(
        "previouslyRegistered.hasPreviouslyRegistered" -> "false"
      )
    )
    previouslyRegisteredForm.bind(js).fold(
      hasErrors => {
        fail("Should have errored out.")
      },
      success => {
        val Some(prevReg) = success.previouslyRegistered
        prevReg.hasPreviouslyRegistered should be(false)
      }
    )
  }
}
