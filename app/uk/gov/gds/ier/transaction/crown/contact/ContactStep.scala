package uk.gov.gds.ier.transaction.crown.contact

import controllers.step.crown.routes.{ContactController, WaysToVoteController}
import controllers.step.crown.ConfirmationController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import play.api.mvc.Call
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.InprogressCrown

class ContactStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends CrownStep
  with ContactForms
  with ContactMustache {

  val validation = contactForm
  val previousRoute = Some(WaysToVoteController.get)

  val routes = Routes(
    get = ContactController.get,
    post = ContactController.post,
    editGet = ContactController.editGet,
    editPost = ContactController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    ConfirmationController.confirmationStep
  }
}
