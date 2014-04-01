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

  def prepopulateEmailAddress (application:InprogressCrown):InprogressCrown = {

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

  def template(
      form: ErrorTransformForm[InprogressCrown],
      postEndpoint: Call,
      backEndpoint:Option[Call]): Html = Html.empty

  override def templateWithApplication(
      form: ErrorTransformForm[InprogressCrown],
      call:Call,
      backUrl: Option[Call]) = {
    application:InprogressCrown =>

    val newForm = form.fill(prepopulateEmailAddress (application))

    contactMustache(application, newForm, call, backUrl)
  }

  def nextStep(currentState: InprogressCrown) = {
    ConfirmationController.confirmationStep
  }
}
