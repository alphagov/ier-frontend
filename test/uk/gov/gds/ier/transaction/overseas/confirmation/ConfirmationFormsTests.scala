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
    val errorMessage = Seq("Please complete this step")
    confirmationForm.bind(js).fold(
      hasErrors => {
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("dateLeftUk") should be(errorMessage)
        hasErrors.errorMessages("firstTimeRegistered") should be(errorMessage)
        hasErrors.errorMessages("previouslyRegistered") should be(errorMessage)
        hasErrors.errorMessages("lastRegisteredToVote") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("lastUkAddress") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(12)
        hasErrors.errors.size should be(24)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressOverseas()
    val errorMessage = Seq("Please complete this step")
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("dateLeftUk") should be(errorMessage)
        hasErrors.errorMessages("firstTimeRegistered") should be(errorMessage)
        hasErrors.errorMessages("previouslyRegistered") should be(errorMessage)
        hasErrors.errorMessages("lastRegisteredToVote") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("lastUkAddress") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(12)
        hasErrors.errors.size should be(24)
      },
      success => fail("Should have errored out.")
    )
  }

}
