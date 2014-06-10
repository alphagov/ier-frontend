package uk.gov.gds.ier.feedback

import com.google.inject.Inject
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.logging.Logging
import play.api.mvc._
import controllers.routes._
import uk.gov.gds.ier.config.Config

class FeedbackThankYouPage @Inject ()(
    val config: Config,
    val remoteAssets: RemoteAssets)
  extends Controller
  with FeedbackForm
  with FeedbackThankYouMustache
  with Logging {

  def get(sourcePath: Option[String]) = Action { implicit request =>
    logger.debug(s"GET request for ${request.path}")
    val form = feedbackThankYouGetForm.bindFromRequest()
    Ok(mustache(
      form,
      controllers.routes.RegisterToVoteController.registerToVote,
      FeedbackRequest()
    ).html)
  }
}

