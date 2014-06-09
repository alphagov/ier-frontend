package uk.gov.gds.ier.feedback

import com.google.inject.Inject
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.logging.Logging
import play.api.mvc._
import controllers.routes._

class FeedbackThankYouPage @Inject ()(
    val remoteAssets: RemoteAssets)
  extends Controller
  with FeedbackForm
  with FeedbackThankYouMustache
  with Logging {

  def get(sourcePath: Option[String]) = Action { implicit request =>
    logger.debug(s"GET request for ${request.path}")
    val form = feedbackThankYouGetForm.bindFromRequest()
    //val sourcePath = form(keys.sourcePath).value.getOrElse("")
    Ok(mustache(
      form,
      FeedbackThankYouController.post,
      FeedbackRequest()
    ).html)
  }

  def post() = Action { implicit request =>
    logger.debug(s"POST request for ${request.path}")
    feedbackThankYouPostForm.bindFromRequest().fold(
      hasErrors => {
        logger.debug(s"Form binding error on FeedbackThankYouPage, returning back to GET")
        val sourcePath = hasErrors(keys.sourcePath).value
        Redirect(FeedbackThankYouController.get(sourcePath))
      },
      success => {
        if (success.sourcePath.isDefined) {
          logger.debug(s"Return to transaction to ${success.sourcePath}")
          Redirect(success.sourcePath.get)
        } else {
          // in reality should not get here, exit variant of ThankYou page does not have a submit button
          logger.debug(s"No sourcePath, returning back to GET")
          Redirect(FeedbackThankYouController.get(None))
        }
      }
    )
  }
}

