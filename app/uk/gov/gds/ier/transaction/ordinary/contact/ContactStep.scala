package uk.gov.gds.ier.transaction.ordinary.contact

import controllers.step.ordinary.routes.{ContactController, PostalVoteController}
import controllers.step.ordinary.ConfirmationController
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.validation._
import play.api.mvc.{SimpleResult, Call}
import uk.gov.gds.ier.model._
import play.api.templates.Html

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PostalVote
import uk.gov.gds.ier.model.PostalVoteDeliveryMethod
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some

class ContactStep @Inject ()(val serialiser: JsonSerialiser,
                             val config: Config,
                             val encryptionService : EncryptionService,
                             val encryptionKeys : EncryptionKeys)
  extends OrdinaryStep
  with ContactForms {

  val validation = contactForm
  val previousRoute = Some(PostalVoteController.get)

  val routes = Routes(
    get = ContactController.get,
    post = ContactController.post,
    editGet = ContactController.editGet,
    editPost = ContactController.editPost
  )

  override def get(implicit manifest: Manifest[InprogressOrdinary]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET request for ${request.path}")
      Ok(template(InProgressForm(validation.fill(prepopulateEmailAddress(application))), routes.post, previousRoute))
  }

  def prepopulateEmailAddress (application:InprogressOrdinary):InprogressOrdinary = {
    application.postalVote match {
      case Some(PostalVote(_,Some(PostalVoteDeliveryMethod(_,Some(emailAddress))))) => {
        application.contact match {
          case Some(Contact(_,_,Some(_))) => {
            val updatedApplication = application.copy(
              contact = Some (application.contact.get.copy (
                  email = Some (application.contact.get.email.get.copy(
                        detail = Some(emailAddress)
                     )
                  )
                )
              )
            )
            updatedApplication
          }
          case Some(Contact(_,_,None)) => {
            val updatedApplication = application.copy(
              contact = Some (application.contact.get.copy (
                  email = Some(ContactDetail(false,Some(emailAddress)))
                )
              )
            )
            updatedApplication
          }
          case None => {
            val updatedApplication = application.copy(
              contact = Some (Contact(false, None, Some(ContactDetail(false,Some(emailAddress)))))
            )
            updatedApplication
          }
        }
      }
      case _ => application
    }
  }

  def template(form:InProgressForm[InprogressOrdinary], call:Call, backUrl: Option[Call]): Html = {
    views.html.steps.contact(form, call, backUrl.map(_.url))
  }

  def nextStep(currentState: InprogressOrdinary) = {
    ConfirmationController.confirmationStep
  }
}
