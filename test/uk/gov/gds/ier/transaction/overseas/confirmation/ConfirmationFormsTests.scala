package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.InprogressOverseas
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.JsNull
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}

class ConfirmationFormTests
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  it should "error out on empty json" in {
    val js = JsNull
    confirmationForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("dateLeftUk") should be(Seq("Please complete this step"))
        hasErrors.errorMessages("firstTimeRegistered") should be(Seq("Please complete this step"))
        hasErrors.errorMessages("previouslyRegistered") should be(Seq("Please complete this step"))
        hasErrors.errorMessages("registeredAddress") should be(Seq("Please complete this step"))
        hasErrors.errorMessages("dateOfBirth") should be(Seq("Please complete this step"))
        hasErrors.globalErrorMessages should be(Seq("Please complete this step", "Please complete this step",
          "Please complete this step","Please complete this step","Please complete this step"))
        hasErrors.errors.size should be(10)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressOverseas()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        hasErrors.errorMessages("dateLeftUk") should be(Seq("Please complete this step"))
        hasErrors.errorMessages("firstTimeRegistered") should be(Seq("Please complete this step"))
        hasErrors.errorMessages("previouslyRegistered") should be(Seq("Please complete this step"))
        hasErrors.errorMessages("registeredAddress") should be(Seq("Please complete this step"))
        hasErrors.errorMessages("dateOfBirth") should be(Seq("Please complete this step"))
        hasErrors.globalErrorMessages should be(Seq("Please complete this step", "Please complete this step",
          "Please complete this step","Please complete this step","Please complete this step"))
        hasErrors.errors.size should be(10)
      },
      success => fail("Should have errored out.")
    )
  }

}
