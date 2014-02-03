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

//  def prepopulateEmailAddress (application:InprogressOverseas):InprogressOverseas = {
//
//    val emailAddress = application.postalVote.flatMap( pvote => pvote.deliveryMethod ).flatMap(deliveryMethod => deliveryMethod.emailAddress)
//    val emailContactDetails = application.contact.flatMap( contact => contact.email ).getOrElse(ContactDetail(false,emailAddress))
//    val newContact = application.contact match {
//      case Some(contact) if contact.email.exists(_.detail.isDefined) => contact
//      case Some(contact) => contact.copy(email = Some(emailContactDetails))
//      case None => Contact(false, None, Some(ContactDetail(false,emailAddress)))
//    }
//    application.copy(contact = Some(newContact))
//  }

//  def template(form:InProgressForm[InprogressOverseas], call:Call, backUrl: Option[Call]): Html = {
//    val newForm = form.form.value match {
//      case Some(application) => form.copy(form = form.form.fill(prepopulateEmailAddress (application)))
//      case None => form
//    }
//    views.html.steps.contact(newForm, call, backUrl.map(_.url))
//  }

  def template(form: InProgressForm[InprogressOverseas], postEndpoint: Call, backEndpoint:Option[Call]): Html = {
    contactMustache(form.form, postEndpoint, backEndpoint)
  }

  def nextStep(currentState: InprogressOverseas) = {
    ConfirmationController.confirmationStep
  }
}
