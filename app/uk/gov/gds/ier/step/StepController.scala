package uk.gov.gds.ier.step

import uk.gov.gds.ier.session.SessionHandling
import play.api.mvc.Controller
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}

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

trait StepController [T <: InprogressApplication[T]]
  extends SessionHandling[T]
  with Step[T]
  with FlowController[T]
  with Controller
  with ErrorMessages
  with FormKeys
  with Logging {
  self: WithSerialiser
    with WithConfig
    with WithEncryption
    with StepTemplate[T] =>

  val validation: ErrorTransformForm[T]
  val confirmationRoute: Call
  val previousRoute: Option[Call]

  val onSuccess: FlowControl = TransformApplication { application => application} andThen GoToNextIncompleteStep()

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

  def get(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    implicit request => application =>
      logger.debug(s"GET request for ${request.path}")
      Ok(mustache(validation.fill(application), routes.post, previousRoute, application).html)
  }

  def postMethod(postCall:Call, backUrl:Option[Call])(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    implicit request => application =>
      logger.debug(s"POST request for ${request.path}")

      val dataFromApplication = validation.fill(application).data
      val dataFromRequest = validation.bindFromRequest().data

      validation.bind(dataFromApplication ++ dataFromRequest).fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          Ok(mustache(hasErrors, postCall, backUrl, application).html) storeInSession application
        },
        success => {
          logger.debug(s"Form binding successful")
          val (mergedApplication, result) = onSuccess(success.merge(application), this)
          Redirect(result.routes.get) storeInSession mergedApplication
        }
      )
  }

  def post(implicit manifest: Manifest[T]) = postMethod(routes.post, previousRoute)

  def editPost(implicit manifest: Manifest[T]) = postMethod(routes.editPost, Some(confirmationRoute))

  def editGet(implicit manifest: Manifest[T]) = ValidSession requiredFor {
    implicit request => application =>
      logger.debug(s"GET edit request for ${request.path}")
      Ok(mustache(validation.fill(application), routes.editPost, Some(confirmationRoute), application).html)
  }
}
