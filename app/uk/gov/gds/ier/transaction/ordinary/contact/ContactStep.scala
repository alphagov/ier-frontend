package uk.gov.gds.ier.transaction.ordinary.contact

import controllers.step.ordinary.routes.ContactController
import controllers.step.ordinary.ConfirmationController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}

class ContactStep @Inject ()(val serialiser: JsonSerialiser,
                             val config: Config,
                             val encryptionService : EncryptionService,
                             val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with ContactForms {

  val validation = contactForm
  val editPostRoute = ContactController.editPost
  val stepPostRoute = ContactController.post

  val routes = Routes(
    get = ContactController.get,
    post = ContactController.post,
    edit = ContactController.editGet,
    editPost = ContactController.editPost
  )

  def template(form:InProgressForm[InprogressOrdinary], call:Call): Html = {
    views.html.steps.contact(form, call)
  }

  def nextStep(currentState: InprogressOrdinary) = {
    ConfirmationController.confirmationStep
  }
}

