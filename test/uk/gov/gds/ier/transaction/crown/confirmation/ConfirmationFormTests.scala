package uk.gov.gds.ier.transaction.crown.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{InprogressCrown, WaysToVote, WaysToVoteType, PostalOrProxyVote}
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
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("statement") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("previousAddress") should be (errorMessage)
        hasErrors.errorMessages("nationality") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("job") should be(errorMessage)
        hasErrors.errorMessages("contactAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(13)
        hasErrors.errors.size should be(26)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressCrown()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("statement") should be(errorMessage)
        hasErrors.errorMessages("address") should be(errorMessage)
        hasErrors.errorMessages("nationality") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousAddress") should be (errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("job") should be(errorMessage)
        hasErrors.errorMessages("contactAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(13)
        hasErrors.errors.size should be(26)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "succeed on waysToVote if postalOrProxy filled (InPerson)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.InPerson)),
      postalOrProxyVote = None
    )
    confirmationForm.fillAndValidate(application).hasErrors should be(false)
  }

  it should "succeed on waysToVote if postalOrProxy filled (ByPost)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    )
    confirmationForm.fillAndValidate(application).hasErrors should be(false)
  }

  it should "succeed on waysToVote if postalOrProxy filled (ByProxy)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = Some(PostalOrProxyVote(
        typeVote = WaysToVoteType.ByPost,
        postalVoteOption = Some(false),
        deliveryMethod = None
      ))
    )
    confirmationForm.fillAndValidate(application).hasErrors should be(false)
  }

  it should "error out on waysToVote if postalOrProxy not filled (ByPost)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByPost)),
      postalOrProxyVote = None
    )
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on waysToVote if postalOrProxy not filled (ByProxy)" in {
    val application = completeCrownApplication.copy(
      waysToVote = Some(WaysToVote(WaysToVoteType.ByProxy)),
      postalOrProxyVote = None
    )
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
      },
      success => fail("Should have errored out.")
    )
  }
}
