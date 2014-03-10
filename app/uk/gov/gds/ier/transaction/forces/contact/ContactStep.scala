package uk.gov.gds.ier.transaction.forces.contact

import controllers.step.forces.routes.{ContactController, WaysToVoteController}
import controllers.step.forces.ConfirmationController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import play.api.mvc.Call
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.validation.InProgressForm

class ContactStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val encryptionKeys : EncryptionKeys)

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

  def prepopulateEmailAddress (application:InprogressForces):InprogressForces = {

    val emailAddress = 
      (for (voteOption <- application.postalOrProxyVote;
    	deliveryMethod <- voteOption.deliveryMethod)
        yield deliveryMethod.emailAddress).flatten
     
    val emailContactDetails = application.contact.flatMap( contact => contact.email )
      .getOrElse(ContactDetail(false,emailAddress))
    
    val newContact = application.contact match {
      case Some(contact) if contact.email.exists(_.detail.isDefined) => contact
      case Some(contact) => contact.copy(email = Some(emailContactDetails))
      case None => Contact(false, None, Some(ContactDetail(false,emailAddress)))
    }
    application.copy(contact = Some(newContact))
  }

  def template(form:InProgressForm[InprogressForces], call:Call, 
      backEndpoint:Option[Call]): Html = {
    val newForm = form.form.value match {
      case Some(application) => form.copy(form = form.form.fill(prepopulateEmailAddress (application)))
      case None => form
    }
    contactMustache(newForm.form, call, backEndpoint)
  }

  def nextStep(currentState: InprogressForces) = {
    ConfirmationController.confirmationStep
  }
}
