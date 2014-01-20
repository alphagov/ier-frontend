package uk.gov.gds.ier.transaction.ordinary.postalVote

import controllers.step.ordinary.ContactController
import controllers.step.ordinary.routes.{PostalVoteController, OpenRegisterController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.OrdinaryStep
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PostalVote
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some

class PostalVoteStep @Inject ()(val serialiser: JsonSerialiser,
                                      val config: Config,
                                      val encryptionService : EncryptionService,
                                      val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with PostalVoteForms {

  val validation = postalVoteForm
  val previousRoute = Some(OpenRegisterController.get)

  val routes = Routes(
    get = PostalVoteController.get,
    post = PostalVoteController.post,
    editGet = PostalVoteController.editGet,
    editPost = PostalVoteController.editPost
  )

  override def get(implicit manifest: Manifest[InprogressOrdinary]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET request for ${request.path}")
      Ok(template(InProgressForm(validation.fill(prepopulateEmailAddress(application))), routes.post, previousRoute))
  }

  def prepopulateEmailAddress (application:InprogressOrdinary):InprogressOrdinary = {
    application.contact match {
      case Some(Contact(_,_,Some(ContactDetail(_,Some(emailAddress))))) => {
        application.postalVote match {
          case Some(PostalVote(postalVoteOption, deliveryMethod)) => {
           deliveryMethod match {
              case Some(PostalVoteDeliveryMethod(deliveryMethod,postalVoteMailAddress)) =>  {
                if (!postalVoteMailAddress.isDefined) {
                  val updatedApplication = application.copy(
                    postalVote = Some(PostalVote(postalVoteOption, Some(PostalVoteDeliveryMethod(deliveryMethod, Some(emailAddress)))))
                  )
                  updatedApplication
                }
                else application
              }
              case None => {
                val updatedApplication = application.copy(
                  postalVote = Some(PostalVote(postalVoteOption, Some(PostalVoteDeliveryMethod(None, Some(emailAddress)))))
                )
                updatedApplication
              }
            }
          }
          case None => {
            val updatedApplication = application.copy(
              postalVote = Some(PostalVote(false, Some(PostalVoteDeliveryMethod(None,Some(emailAddress)))))
            )
            updatedApplication
          }
        }
      }
      case _ => application
    }
  }

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    views.html.steps.postalVote(form, call, backUrl.map(_.url))
  }
  def nextStep(currentState: InprogressOrdinary) = {
    ContactController.contactStep
  }

}

