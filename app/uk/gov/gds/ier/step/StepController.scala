package uk.gov.gds.ier.step

import uk.gov.gds.ier.session.{SessionHandling, CacheBust}
import play.api.mvc.Controller
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}

trait Step[T] {
  val routing: Routes

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

  implicit val manifestOfT: Manifest[T]

  val validation: ErrorTransformForm[T]
  val confirmationRoute: Call

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

  def get = CacheBust {
    ValidSession requiredFor { implicit request => application =>
      logger.debug(s"GET request for ${request.path}")
      Ok(mustache(validation.fill(application), routing.post, application).html)
    }
  }

  def postMethod(postCall:Call) = CacheBust {
    ValidSession requiredFor { implicit request => application =>
      logger.debug(s"POST request for ${request.path}")

      val dataFromApplication = validation.fill(application).data
      val dataFromRequest = validation.bindFromRequest().data

      validation.bind(dataFromApplication ++ dataFromRequest).fold(
        hasErrors => {
          logger.debug(s"Form binding error: ${hasErrors.prettyPrint.mkString(", ")}")
          Ok(mustache(hasErrors, postCall, application).html) storeInSession application
        },
        success => {
          logger.debug(s"Form binding successful")
          val (mergedApplication, result) = onSuccess(success.merge(application), this)
          Redirect(result.routing.get) storeInSession mergedApplication
        }
      )
    }
  }

  def post = postMethod(routing.post)

  def editPost = postMethod(routing.editPost)

  def editGet = CacheBust {
    ValidSession requiredFor { implicit request => application =>
      logger.debug(s"GET edit request for ${request.path}")
      Ok(mustache(validation.fill(application), routing.editPost, application).html)
    }
  }
}
