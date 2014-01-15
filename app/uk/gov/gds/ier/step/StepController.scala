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
  def template(form: InProgressForm[T], call: Call, backUrl: Option[String]):Html
  def goToNext(currentState: T):SimpleResult
  def backToPrevious(currentState: T): SimpleResult


  //Can override this method if you like
  def goToConfirmation(currentState: T):SimpleResult = {
    Redirect(confirmationRoute)
  }

  def editPage:(InProgressForm[T], Option[String]) => Html = {
    (form, backUrl) => template(form, editPostRoute, backUrl)
  }

  def stepPage:(InProgressForm[T], Option[String]) => Html = {
    (form, backUrl) => template(form, stepPostRoute, backUrl)
  }

  def get(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET request for ${request.path}")
      Ok(stepPage(InProgressForm(validation.fill(application)), application.backUrl))
  }

  def post(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    implicit request => application =>
      logger.debug(s"POST request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          Ok(stepPage(InProgressForm(hasErrors), application.backUrl)) storeInSession application
        },
        success => {
          logger.debug(s"Form binding successful")
          val mergedApplication = success.merge(application)
          val backUrl = request.uri
          
          goToNext(mergedApplication) storeInSession mergedApplication.withBackUrl(request.uri)
        }
      )
  }

  def editGet(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET edit request for ${request.path}")
      Ok(editPage(InProgressForm(validation.fill(application)), application.backUrl))
  }

  def editPost(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    implicit request => application =>
      logger.debug(s"POST edit request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          Ok(editPage(InProgressForm(hasErrors), application.backUrl)) storeInSession application
        },
        success => {
          logger.debug(s"Form binding successful")
          val mergedApplication = success.merge(application)
          goToConfirmation(mergedApplication) storeInSession mergedApplication.withBackUrl(request.uri)
        }
      )
  }
  def back(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET request for ${request.path}")
      backToPrevious(application) 
//      Ok(stepPage(InProgressForm(validation.fill(application)), application.backUrl))
  }
}
