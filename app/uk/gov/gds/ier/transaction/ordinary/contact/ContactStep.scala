package uk.gov.gds.ier.transaction.ordinary.contact

import controllers.step.ordinary.routes.{ContactController, PostalVoteController}
import controllers.step.ordinary.ConfirmationController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.OrdinaryStep
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some

class ContactStep @Inject ()(val serialiser: JsonSerialiser,
                             val config: Config,
                             val encryptionService : EncryptionService)
  extends OrdinaryStep
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

  def prepopulateEmailAddress (application:InprogressOrdinary):InprogressOrdinary = {

    val emailAddress = application.postalVote.flatMap( pvote => pvote.deliveryMethod ).flatMap(deliveryMethod => deliveryMethod.emailAddress)
    val emailContactDetails = application.contact.flatMap( contact => contact.email ).getOrElse(ContactDetail(false,emailAddress))
    val newContact = application.contact match {
      case Some(contact) if contact.email.exists(_.detail.isDefined) => contact
      case Some(contact) => contact.copy(email = Some(emailContactDetails))
      case None => Contact(false, None, Some(ContactDetail(false,emailAddress)))
    }
    application.copy(contact = Some(newContact))
  }

  def template(form:InProgressForm[InprogressOrdinary], call:Call, 
      backEndpoint:Option[Call]): Html = Html.empty

  override def templateWithApplication(
      form: InProgressForm[InprogressOrdinary],
      call: Call,
      backUrl: Option[Call]):InprogressOrdinary => Html = {
    application:InprogressOrdinary =>
      val newForm = form.copy(form = form.form.fill(prepopulateEmailAddress (application)))
      contactMustache(newForm.form, call, backUrl)
  }

  def nextStep(currentState: InprogressOrdinary) = {
    ConfirmationController.confirmationStep
  }
}
