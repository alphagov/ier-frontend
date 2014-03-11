package uk.gov.gds.ier.transaction.ordinary.postalVote

import controllers.step.ordinary.ContactController
import controllers.step.ordinary.routes.{PostalVoteController, OpenRegisterController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.OrdinaryStep
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PostalVote
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some

class PostalVoteStep @Inject ()(val serialiser: JsonSerialiser,
                                      val config: Config,
                                      val encryptionService : EncryptionService)
  extends OrdinaryStep
  with PostalVoteForms 
  with PostalVoteMustache {

  val validation = postalVoteForm
  val previousRoute = Some(OpenRegisterController.get)

  val routes = Routes(
    get = PostalVoteController.get,
    post = PostalVoteController.post,
    editGet = PostalVoteController.editGet,
    editPost = PostalVoteController.editPost
  )

  def prepopulateEmailAddress (application:InprogressOrdinary):InprogressOrdinary = {
    val emailAddress = application.contact.flatMap( contact => contact.email ).flatMap(email => email.detail)
    val deliveryMethod = application.postalVote.flatMap( pvote => pvote.deliveryMethod ).getOrElse(PostalVoteDeliveryMethod(None, emailAddress))
    val newPostalVote = application.postalVote match {
      case Some(postalVote) if postalVote.deliveryMethod.exists(_.emailAddress.isDefined) => postalVote
      case Some(postalVote) => postalVote.copy(deliveryMethod = Some(deliveryMethod))
      case None => PostalVote(None, Some(deliveryMethod))
    }
    application.copy(postalVote = Some(newPostalVote))
  }

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    val newForm = form.form.value match {
      case Some(application) => form.copy(form = form.form.fill(prepopulateEmailAddress (application)))
      case None => form
    }
    postalVoteMustache(newForm.form, call, backUrl)
  }

 override def postSuccess(currentState: InprogressOrdinary):InprogressOrdinary = {
    currentState.postalVote match {
      case Some(PostalVote(Some(false), _)) =>
          currentState.copy(postalVote = Some(PostalVote(Some(false), None)))
      case _ => currentState
    }
  }  
  
  def nextStep(currentState: InprogressOrdinary) = {
    ContactController.contactStep
  }

}

