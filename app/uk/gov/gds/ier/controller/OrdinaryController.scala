package uk.gov.gds.ier.controller

import play.api.mvc._
import controllers._
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.session.SessionHandling
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import play.api.data.Form
import play.api.templates.Html
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}

trait OrdinaryController
  extends StepController[InprogressOrdinary]
  with WithSerialiser
  with WithConfig
  with WithEncryption {
  def factoryOfT() = InprogressOrdinary()
  val confirmationRoute = controllers.step.ordinary.routes.ConfirmationController.get
}

trait ConfirmationStep[T <: InprogressApplication[T]]
  extends SessionHandling[T]
  with Controller
  with Logging
  with WithSerialiser
  with WithConfig
  with WithEncryption {

  val validation: ErrorTransformForm[T]
  def template(form:InProgressForm[T]): Html
  def get:Action[AnyContent]
  def post:Action[AnyContent]
}


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
  def template(form: InProgressForm[T], call: Call):Html
  def goToNext(currentState: T):SimpleResult


  //Can override this method if you like
  def goToConfirmation(currentState: T):SimpleResult = {
    Redirect(confirmationRoute)
  }

  def editPage:InProgressForm[T] => Html = {
    form => template(form, editPostRoute)
  }

  def stepPage:InProgressForm[T] => Html = {
    form => template(form, stepPostRoute)
  }

  def get(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET request for ${request.path}")
      Ok(stepPage(InProgressForm(validation.fill(application))))
  }

  def post(implicit manifest: Manifest[T]) = ValidSession storeAfter {
    implicit request => application =>
      logger.debug(s"POST request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          (Ok(stepPage(InProgressForm(hasErrors))), application)
        },
        success => {
          logger.debug(s"Form binding successful")
          val mergedApplication = success.merge(application)
          (goToNext(mergedApplication), mergedApplication)
        }
      )
  }

  def editGet(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET edit request for ${request.path}")
      Ok(editPage(InProgressForm(validation.fill(application))))
  }

  def editPost(implicit manifest: Manifest[T]) = ValidSession storeAfter {
    implicit request => application =>
      logger.debug(s"POST edit request for ${request.path}")
      validation.bindFromRequest().fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          (Ok(editPage(InProgressForm(hasErrors))), application)
        },
        success => {
          logger.debug(s"Form binding successful")
          val mergedApplication = success.merge(application)
          (goToConfirmation(mergedApplication), mergedApplication)
        }
      )
  }


}
