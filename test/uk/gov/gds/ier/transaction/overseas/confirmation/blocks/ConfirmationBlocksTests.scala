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

class ConfirmationBlocksTests
  extends FlatSpec
  with Matchers
  with ConfirmationForms
  with WithSerialiser
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val serialiser = jsonSerialiser

  behavior of "ConfirmationBlocks.applicantBlocks"

  it should "return correct blocks for young voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteYoungApplication)
    val blocks = new ConfirmationBlocks(filledForm)

    blocks.applicantBlocks() should be(
      List(
        blocks.previouslyRegistered,
        blocks.dateLeftUk,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for new voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteNewApplication)
    val blocks = new ConfirmationBlocks(filledForm)

    blocks.applicantBlocks() should be(
      List(
        blocks.previouslyRegistered,
        blocks.dateLeftUk,
        blocks.lastUkAddress,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for renewer voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteRenewerApplication)
    val blocks = new ConfirmationBlocks(filledForm)

    blocks.applicantBlocks() should be(
      List(
        blocks.previouslyRegistered,
        blocks.dateLeftUk,
        blocks.lastUkAddress,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for crown voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteCrownApplication)
    val blocks = new ConfirmationBlocks(filledForm)

    blocks.applicantBlocks() should be(
      List(
        blocks.previouslyRegistered,
        blocks.dateLeftCrown,
        blocks.lastUkAddress,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for council voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteCouncilApplication)
    val blocks = new ConfirmationBlocks(filledForm)

    blocks.applicantBlocks() should be(
      List(
        blocks.previouslyRegistered,
        blocks.dateLeftCouncil,
        blocks.lastUkAddress,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }

  it should "return correct blocks for forces voter" in {
    val filledForm = confirmationForm.fillAndValidate(incompleteForcesApplication)
    val blocks = new ConfirmationBlocks(filledForm)

    blocks.applicantBlocks() should be(
      List(
        blocks.previouslyRegistered,
        blocks.dateLeftForces,
        blocks.lastUkAddress,
        blocks.passport,
        blocks.name,
        blocks.previousName,
        blocks.dateOfBirth,
        blocks.nino,
        blocks.address,
        blocks.openRegister,
        blocks.waysToVote,
        blocks.contact
      )
    )
  }
}
