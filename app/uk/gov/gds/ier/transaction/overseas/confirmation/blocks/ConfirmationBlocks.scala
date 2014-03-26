package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.model.ApplicationType._
import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.validation.{ErrorTransformForm, Key}
import controllers.step.overseas.routes
import scala.util.Try

case class ConfirmationQuestion(
    content: String,
    title: String,
    editLink: String,
    changeName: String
)

trait ConfirmationBlock
  extends StepMustache
  with Logging
  with OverseasFormImplicits {

  val form: ErrorTransformForm[InprogressOverseas]

  val completeThisStepMessage = "<div class=\"validation-message visible\">" +
    "Please complete this step" +
    "</div>"

  def ifComplete(key:Key)(confirmationHtml: => String) = {
    if (form(key).hasErrors) {
      completeThisStepMessage
    } else {
      confirmationHtml
    }
  }
}

class ConfirmationBlocks(val form:ErrorTransformForm[InprogressOverseas])
  extends ConfirmationBlock
  with PreviouslyRegisteredBlocks
  with LastUkAddressBlocks
  with DateLeftBlocks
  with NinoBlocks
  with AddressBlocks
  with DateOfBirthBlocks
  with OpenRegisterBlocks
  with NameBlocks
  with WaysToVoteBlocks
  with ContactBlocks
  with PassportBlocks
  with ParentNameBlocks
  with ParentsAddressBlocks {

  def parentBlocks() = {
    form.identifyApplication match {
      case YoungVoter => List(
        parentName,
        parentPreviousName,
        parentsAddress
      )
      case _ => List.empty
    }
  }

  def applicantBlocks() = {
    form.identifyApplication match {
      case YoungVoter => youngVoterBlocks()
      case NewVoter => newVoterBlocks()
      case SpecialVoter => specialVoterBlocks()
      case RenewerVoter => renewerVoterBlocks()
      case _ => List.empty
    }
  }

  def youngVoterBlocks() = {
    List(
      previouslyRegistered,
      dateLeftUk,
      passport,
      name,
      previousName,
      dateOfBirth,
      nino,
      address,
      openRegister,
      waysToVote,
      contact
    )
  }

  def newVoterBlocks() = {
    List(
      previouslyRegistered,
      dateLeftUk,
      lastUkAddress,
      passport,
      name,
      previousName,
      dateOfBirth,
      nino,
      address,
      openRegister,
      waysToVote,
      contact
    )
  }

  def renewerVoterBlocks() = {
    List(
      previouslyRegistered,
      dateLeftUk,
      lastUkAddress,
      name,
      previousName,
      dateOfBirth,
      nino,
      address,
      openRegister,
      waysToVote,
      contact
    )
  }

  def specialVoterBlocks() = {
    List(
      previouslyRegistered,
      dateLeftSpecial,
      lastUkAddress,
      passport,
      name,
      previousName,
      dateOfBirth,
      nino,
      address,
      openRegister,
      waysToVote,
      contact
    )
  }
}

