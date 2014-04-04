package uk.gov.gds.ier.transaction.ordinary.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import org.scalatest.{Matchers, FlatSpec}
import play.api.libs.json.JsNull
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

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
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("nationality") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("otherAddress") should be(errorMessage)
        hasErrors.errorMessages("previousAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("postalVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(11)
        hasErrors.errors.size should be(22)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressOrdinary()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("nationality") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("otherAddress") should be(errorMessage)
        hasErrors.errorMessages("previousAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("postalVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(11)
        hasErrors.errors.size should be(22)
      },
      success => fail("Should have errored out.")
    )
  }

}
