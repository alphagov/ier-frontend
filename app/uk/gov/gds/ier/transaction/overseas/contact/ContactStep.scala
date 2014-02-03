package uk.gov.gds.ier.transaction.overseas.contact

import controllers.step.overseas.routes.{ContactController, PostalVoteController}
import controllers.step.overseas.ConfirmationController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import play.api.mvc.Call
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some

class ContactStep @Inject ()(val serialiser: JsonSerialiser,
                             val config: Config,
                             val encryptionService : EncryptionService,
                             val encryptionKeys : EncryptionKeys)
  extends OverseaStep
  with ContactForms
  with ContactMustache {

  val validation = contactForm
  val previousRoute = Some(PostalVoteController.get)

  val routes = Routes(
    get = ContactController.get,
    post = ContactController.post,
    editGet = ContactController.editGet,
    editPost = ContactController.editPost
  )

  def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    contactMustache(form.form, postEndpoint, backEndpoint)
  }

  def nextStep(currentState: InprogressOverseas) = {
    ConfirmationController.confirmationStep
  }
}
