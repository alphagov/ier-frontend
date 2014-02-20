package uk.gov.gds.ier.transaction.overseas.confirmation

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
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

  behavior of "ConfirmationForms.validateBaseSetRequired"
  it should "error out on empty json" in {
    val js = JsNull
    confirmationForm.bind(js).fold(
      hasErrors => {
        hasErrors.globalErrorMessages.count(_ == "Base set criteria not met") should be(1)
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  it should "error out on empty application" in {
    val application = InprogressOverseas()
    confirmationForm.fillAndValidate(application).fold(
      hasErrors => {
        hasErrors.globalErrorMessages.count(_ == "Base set criteria not met") should be(1)
        hasErrors.errors.size should be(2)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "ConfirmationForms.validateYoungVoter"

  it should "error out on missing young voter fields" in {
    val twentyYearsAgo = new DateTime().minusYears(20).getYear
    val fiveYearsAgo = new DateTime().minusYears(5).getYear
    val youngVoter = InprogressOverseas(
      dob = Some(DOB(year = twentyYearsAgo, month = 12, day = 1)),
      dateLeftUk = Some(DateLeft(year = fiveYearsAgo, month = 1))
    )

    confirmationForm.fillAndValidate(youngVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
//        hasErrors.errorMessages("parentsAddress") should be(errorMessage)
//        hasErrors.errorMessages("parentsName") should be(errorMessage)
//        hasErrors.errorMessages("parentsPreviousName") should be(errorMessage)
        hasErrors.errorMessages("previouslyRegistered") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("overseasAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("postalOrProxyVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.errorMessages("passport") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(11)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "ConfirmationForm.validateNewVoter"

  it should "error out on missing new voter fields" in {
    val newVoter = InprogressOverseas(
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Ordinary
      ))
    )

    confirmationForm.fillAndValidate(newVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("previouslyRegistered") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("lastUkAddress") should be(errorMessage)
        hasErrors.errorMessages("dateLeftUk") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("overseasAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("postalOrProxyVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.errorMessages("passport") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(14)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "ConfirmationForm.validateSpecialVoter"

  it should "error out on missing new voter fields" in {
    val specialVoter = InprogressOverseas(
      lastRegisteredToVote = Some(LastRegisteredToVote(
        lastRegisteredType = LastRegisteredType.Forces
      ))
    )

    confirmationForm.fillAndValidate(specialVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("previouslyRegistered") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("lastUkAddress") should be(errorMessage)
        hasErrors.errorMessages("dateLeftSpecial") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("overseasAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("postalOrProxyVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.errorMessages("passport") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(14)
      },
      success => fail("Should have errored out.")
    )
  }

  behavior of "ConfirmationForm.validateRenewerVoter"

  it should "error out on missing new voter fields" in {
    val specialVoter = InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(true))
    )

    confirmationForm.fillAndValidate(specialVoter).fold(
      hasErrors => {
        val errorMessage = Seq("Please complete this step")
        hasErrors.errorMessages("name") should be(errorMessage)
        hasErrors.errorMessages("previousName") should be(errorMessage)
        hasErrors.errorMessages("dob") should be(errorMessage)
        hasErrors.errorMessages("dateLeftUk") should be(errorMessage)
        hasErrors.errorMessages("NINO") should be(errorMessage)
        hasErrors.errorMessages("overseasAddress") should be(errorMessage)
        hasErrors.errorMessages("lastUkAddress") should be(errorMessage)
        hasErrors.errorMessages("openRegister") should be(errorMessage)
        hasErrors.errorMessages("waysToVote") should be(errorMessage)
        hasErrors.errorMessages("postalOrProxyVote") should be(errorMessage)
        hasErrors.errorMessages("contact") should be(errorMessage)
        hasErrors.globalErrorMessages.count(_ == "Please complete this step") should be(1)
        hasErrors.errors.size should be(14)
      },
      success => fail("Should have errored out.")
    )
  }
}
