package uk.gov.gds.ier.step

import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.session.SessionHandling
import play.api.mvc.{SimpleResult, Call, Controller}
import uk.gov.gds.ier.validation.{InProgressForm, ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import play.api.templates.Html
import uk.gov.gds.ier.guice.DelegatingController

trait NextStep[T] {
  def goToNext(currentState:T):SimpleResult
}

trait StepController [T <: InprogressApplication[T]]
  extends SessionHandling[T]
  with NextStep[T]
  with Controller
  with ErrorMessages
  with FormKeys
  with Logging {
  self: WithSerialiser
    with WithConfig
    with WithEncryption =>

  val routes: Routes

  val validation: ErrorTransformForm[T]

  def template(form: InProgressForm[T], call: Call):Html
  //Returns true if this step is currently complete
  def isStepComplete(currentState: T):Boolean = {
    val filledForm = validation.fillAndValidate(currentState)
    filledForm.fold(
      error => {
        false
      },
      success => {
        true
      }
    )
  }
  //Inspects the current state of the application and determines which step should be next
  //e.g. 
  //if (currentState.foo == true)
  //  TrueController
  //else 
  //  FalseController
  def nextStep(currentState: T):NextStep[T]
                                                    
  def goToNext(currentState: T):SimpleResult = {
    if (isStepComplete(currentState)) {
      nextStep(currentState).goToNext(currentState)
    } else {
      Redirect(routes.get)
    }
  }

  def editPage:InProgressForm[T] => Html = {
    form => template(form, routes.editPost)
  }

  def stepPage:InProgressForm[T] => Html = {
    form => template(form, routes.post)
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
      logger.debug(s"GET request for ${request.path}")
      Ok(editPage(InProgressForm(validation.fill(application))))
  }

  def editPost(implicit manifest: Manifest[T]) = ValidSession requiredFor {
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
}
