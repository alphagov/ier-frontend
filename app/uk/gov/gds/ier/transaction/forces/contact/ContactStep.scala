package uk.gov.gds.ier.transaction.forces.contact

import controllers.step.forces.routes.{ContactController, WaysToVoteController}
import controllers.step.forces.ConfirmationController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class ContactStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends ForcesStep
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

  def nextStep(currentState: InprogressForces) = {
    ConfirmationController.confirmationStep
  }
}
