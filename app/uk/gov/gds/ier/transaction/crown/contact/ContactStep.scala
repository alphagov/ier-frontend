package uk.gov.gds.ier.transaction.crown.contact

import uk.gov.gds.ier.transaction.crown.CrownControllers
import controllers.step.crown.routes.{ContactController, WaysToVoteController}
import controllers.step.crown.ConfirmationController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import play.api.mvc.Call
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class ContactStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with ContactForms
  with ContactMustache {

  val validation = contactForm

  val routing = Routes(
    get = ContactController.get,
    post = ContactController.post,
    editGet = ContactController.editGet,
    editPost = ContactController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.ConfirmationStep
  }
}
