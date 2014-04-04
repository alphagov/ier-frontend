package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import org.joda.time.DateTime
import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{
  Name,
  PreviousName,
  WaysToVote}
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationForms
import org.joda.time.DateTime
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class PassportBlocksTests
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  behavior of "ConfirmationBlocks.passport"

  it should "return 'complete this' message if not a renewer" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(false))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    val model = passportModel
    model.content should include("Please complete this step")
    model.editLink should be("/register-to-vote/overseas/edit/passport")
  }

  it should "return 'complete this' message if passport details only partially complete" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(false)),
      passport = Some(Passport(true, None, None, None))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    val model = passportModel
    model.content should include("Please complete this step")
    model.editLink should be("/register-to-vote/overseas/edit/passport-details")
  }

  it should "return 'complete this' message if citizen details only partially complete" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      previouslyRegistered = Some(PreviouslyRegistered(false)),
      passport = Some(Passport(false, Some(false), None, None))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    val model = passportModel
    model.content should include("Please complete this step")
    model.editLink should be("/register-to-vote/overseas/edit/citizen-details")
  }
}
