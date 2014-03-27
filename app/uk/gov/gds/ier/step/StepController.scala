package uk.gov.gds.ier.step

import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.session.SessionHandling
import play.api.mvc.{SimpleResult, Call, Controller}
import uk.gov.gds.ier.validation.{InProgressForm, ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import play.api.templates.Html
import play.api.mvc.Results

trait Step[T] {
  val routes: Routes

  //Returns true if this step is currently complete
  def isStepComplete (currentState: T):Boolean
  //Inspects the current state of the application and determines which step should be next
  //e.g.
  //if (currentState.foo == true)
  //  TrueController
  //else
  //  FalseController
  def nextStep(currentState: T):Step[T]
}

object TransformApplication extends Results {

  def apply[T](transform:T=>T): (T, Step[T]) => (T, SimpleResult) = {
    (application, nextStep) => {
      val transformedApplication = transform(application)
      val result = goToNext(transformedApplication, nextStep)
      (transformedApplication, result)
    }
  }

  def goToNext[T] (currentState: T, step: Step[T]):SimpleResult = {
    if (step.isStepComplete(currentState)) {
      val nextStep = step.nextStep(currentState)
      goToNext(currentState, nextStep)
    } else {
      Redirect(step.routes.get)
    }
  }
}

trait StepController [T <: InprogressApplication[T]]
  extends SessionHandling[T]
  with Step[T]
  with Controller
  with ErrorMessages
  with FormKeys
  with Logging {
  self: WithSerialiser
    with WithConfig
    with WithEncryption =>

  val validation: ErrorTransformForm[T]
  val confirmationRoute: Call
  val previousRoute:Option[Call]
  def template(form: InProgressForm[T], call: Call, backUrl: Option[Call]):Html

  val onSuccess: (T, Step[T]) => (T, SimpleResult) = TransformApplication {
    application:T => postSuccess(application)
  }

  def templateWithApplication(form: InProgressForm[T], call: Call, backUrl: Option[Call]):T => Html = {
    application:T => template(form, call, backUrl)
  }

  def isStepComplete (currentState: T):Boolean = {
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

  def postSuccess(currentState: T):T = {
    currentState
  }


  def get(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET request for ${request.path}")
      Ok(templateWithApplication(InProgressForm(validation.fill(application)), routes.post, previousRoute)(application))
  }

  def postMethod(postCall:Call, backUrl:Option[Call])(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    implicit request => application =>
      logger.debug(s"POST request for ${request.path}")

      val dataFromApplication = validation.fill(application).data
      val dataFromRequest = validation.bindFromRequest().data

      validation.bind(dataFromApplication ++ dataFromRequest).fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          Ok(templateWithApplication(InProgressForm(hasErrors), postCall, backUrl)(application)) storeInSession application
        },
        success => {
          logger.debug(s"Form binding successful")
          val (mergedApplication, result) = onSuccess(success.merge(application), this)
          result storeInSession mergedApplication
        }
      )
  }

  def post(implicit manifest: Manifest[T]) = postMethod(routes.post, previousRoute)

  def editPost(implicit manifest: Manifest[T]) = postMethod(routes.editPost, Some(confirmationRoute))

  def editGet(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    request => application =>
      logger.debug(s"GET edit request for ${request.path}")
      Ok(templateWithApplication(InProgressForm(validation.fill(application)), routes.editPost, Some(confirmationRoute))(application))
  }
}
