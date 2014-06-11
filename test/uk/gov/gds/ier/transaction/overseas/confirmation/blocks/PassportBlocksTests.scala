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
import uk.gov.gds.ier.transaction.shared.{BlockError, BlockContent}

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
      lastRegisteredToVote = Some(LastRegisteredToVote(
          LastRegisteredType.Ordinary
      ))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    val model = passportModel
    model.content should be(BlockContent("Please complete this step"))
    model.editLink should be("/register-to-vote/overseas/edit/passport")
  }

  it should "return 'complete this' message if passport details only partially complete" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      lastRegisteredToVote = Some(LastRegisteredToVote(
          LastRegisteredType.Ordinary
      )),
      passport = Some(Passport(true, None, None, None))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    val model = passportModel
    model.content should be(BlockError("Please complete this step"))
    model.editLink should be("/register-to-vote/overseas/edit/passport-details")
  }

  it should "return 'complete this' message if citizen details only partially complete" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      lastRegisteredToVote = Some(LastRegisteredToVote(
          LastRegisteredType.Ordinary
      )),
      passport = Some(Passport(false, Some(false), None, None))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    val model = passportModel
    model.content should be(BlockContent("Please complete this step"))
    model.editLink should be("/register-to-vote/overseas/edit/citizen-details")
  }

  it should "return return correct citizenship reason and date in correct format" in {
    val partialApplication = confirmationForm.fillAndValidate(InprogressOverseas(
      lastRegisteredToVote = Some(LastRegisteredToVote(
        LastRegisteredType.Ordinary
      )),
      passport = Some(Passport(
        hasPassport = false,
        bornInsideUk = Some(false),
        details = None,
        citizen = Some(CitizenDetails(DOB(2000,1,1),"test reason"))
      ))
    ))

    val confirmation = new ConfirmationBlocks(partialApplication)
    val passportModel = confirmation.passport

    val model = passportModel
    model.content should be(BlockContent(List(
      "I became a citizen through: test reason",
      "I became a citizen on:",
      "01 January 2000")))
    model.editLink should be("/register-to-vote/overseas/edit/citizen-details")
  }
}
