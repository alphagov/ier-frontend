package uk.gov.gds.ier.transaction.overseas.contact

import controllers.step.overseas.routes.{ContactController, WaysToVoteController}
import controllers.step.overseas.ConfirmationController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class ContactStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)

  extends OverseaStep
  with ContactForms
  with ContactMustache {

  val validation = contactForm

  val routes = Routes(
    get = ContactController.get,
    post = ContactController.post,
    editGet = ContactController.editGet,
    editPost = ContactController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    ConfirmationController.confirmationStep
  }
}
