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
        val keysToCheck = List ("name", "previousName", "dateLeftUk", "firstTimeRegistered", 
            "previouslyRegistered", "lastRegisteredToVote", "dob", "lastUkAddress", "NINO", 
            "overseasAddress", "openRegister", "waysToVote", "postalVote", "contact")
            
        keysToCheck foreach { key =>
          hasErrors.errorMessages(key).head should be ("Please complete this step")
        }
        hasErrors.globalErrorMessages should be(List.fill(14)("Please complete this step"))
        hasErrors.errors.size should be(28)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressOverseas()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val keysToCheck = List ("name", "previousName", "dateLeftUk", "firstTimeRegistered", 
            "previouslyRegistered", "lastRegisteredToVote", "dob", "lastUkAddress", "NINO", 
            "overseasAddress", "openRegister", "waysToVote", "postalVote", "contact")
            
        keysToCheck foreach { key =>
          hasErrors.errorMessages(key).head should be ("Please complete this step")
        }
        hasErrors.globalErrorMessages should be(List.fill(14)("Please complete this step"))
        hasErrors.errors.size should be(28)
      },
      success => fail("Should have errored out.")
    )
  }
}
