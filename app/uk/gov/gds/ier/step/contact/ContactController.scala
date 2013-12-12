package uk.gov.gds.ier.step.contact

import controllers._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.controller.StepController
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class ContactController @Inject ()(val serialiser: JsonSerialiser,
                                   val config: Config,
                                   val encryptionService : EncryptionService,
                                   val encryptionKeys : EncryptionKeys)
  extends StepController
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with ContactForms {

  val validation = contactForm
  val editPostRoute = step.routes.ContactController.editPost
  val stepPostRoute = step.routes.ContactController.post

  def template(form:InProgressForm, call:Call): Html = {
    views.html.steps.contact(form, call)
  }
  def goToNext(currentState: InprogressApplication): SimpleResult = {
    Redirect(routes.ConfirmationController.get)
  }
}

