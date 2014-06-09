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

  def get(sourcePath: String) = Action { implicit request =>
    logger.debug(s"GET request for ${request.path}")
    val form = feedbackThankYouGetForm.bindFromRequest()
    val sourcePath = form(keys.sourcePath).value.getOrElse("")
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
        Redirect(FeedbackThankYouController.get(sourcePath.getOrElse("")))
      },
      success => {
        logger.debug(s"Return to transaction to ${success.sourcePath}")
        Redirect(success.sourcePath)
      }
    )
  }
}

