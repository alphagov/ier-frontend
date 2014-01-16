package uk.gov.gds.ier.step

import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.session.SessionHandling
import play.api.mvc.{SimpleResult, Call, Controller}
import uk.gov.gds.ier.validation.{InProgressForm, ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import play.api.templates.Html

trait StepController [T <: InprogressApplication[T]]
  extends SessionHandling[T]
  with Controller
  with ErrorMessages
  with FormKeys
  with Logging {
  self: WithSerialiser
    with WithConfig
    with WithEncryption =>

  val validation: ErrorTransformForm[T]
  val editPostRoute: Call
  val stepPostRoute: Call
  val confirmationRoute: Call
  val previousRoute:Option[Call]
  def template(form: InProgressForm[T], postUrl: Call, backUrl: Option[Call]):Html
  def goToNext(currentState: T):SimpleResult


  //Can override this method if you like
  def goToConfirmation(currentState: T):SimpleResult = {
    Redirect(confirmationRoute)
  }

  def editPage:(InProgressForm[T]) => Html = {
    form => template(form, editPostRoute, Some(confirmationRoute))
  }

  def stepPage:(InProgressForm[T]) => Html = {
    form => template(form, stepPostRoute, previousRoute)
  }

  def get(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET request for ${request.path}")
      Ok(stepPage(InProgressForm(validation.fill(application))))
  }

  def post(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    implicit request => application =>
      logger.debug(s"POST request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          Ok(stepPage(InProgressForm(hasErrors))) storeInSession application
        },
        success => {
          logger.debug(s"Form binding successful")
          val mergedApplication = success.merge(application)
          
          goToNext(mergedApplication) storeInSession mergedApplication
        }
      )
  }

  def editGet(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET edit request for ${request.path}")
      Ok(editPage(InProgressForm(validation.fill(application))))
  }

  def editPost(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    implicit request => application =>
      logger.debug(s"POST edit request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          Ok(editPage(InProgressForm(hasErrors))) storeInSession application
        },
        success => {
          logger.debug(s"Form binding successful")
          val mergedApplication = success.merge(application)
          goToConfirmation(mergedApplication) storeInSession mergedApplication
        }
      )
  }
}
