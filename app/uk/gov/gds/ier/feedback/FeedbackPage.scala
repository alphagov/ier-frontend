package uk.gov.gds.ier.feedback

import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.assets.RemoteAssets
import controllers.routes.FeedbackController
import uk.gov.gds.ier.logging.Logging
import play.api.mvc._
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithConfig}

class FeedbackPage @Inject ()(
    val config: Config,
    val remoteAssets: RemoteAssets,
    val feedbackService: FeedbackService)
  extends Controller
  with FeedbackForm
  with FeedbackMustache
  with Logging
  with WithConfig
  with WithRemoteAssets {

  val validation = feedbackForm

  def get(sourcePath: Option[String]) = Action { implicit request =>
    logger.debug(s"GET request for ${request.path} with source path ${sourcePath}")
    Ok(FeedbackPage(
      postUrl = FeedbackController.post(sourcePath).url
    ))
  }

  def post(sourcePath: Option[String]) = Action { implicit request =>
    logger.debug(s"POST request for ${request.path}")
    validation.bindFromRequest().fold(
      hasErrors => {
        logger.debug(s"Form binding error: ${hasErrors}")
        Redirect(FeedbackController.thankYou(sourcePath))
      },
      success => {
        logger.debug(s"Form binding successful, proceed with submitting feedback")
        val browserDetails = getBrowserAndOsDetailsIfPresent(request)
        feedbackService.submit(success, browserDetails)
        Redirect(FeedbackController.thankYou(sourcePath))
      }
    )
  }

  def thankYou(sourcePath: Option[String]) = Action { implicit request =>
    logger.debug(s"GET request for ${request.path}")
    Ok(ThankYouPage(
      sourcePath = sourcePath.getOrElse(config.ordinaryStartUrl)
    ))
  }

  private[feedback] def getBrowserAndOsDetailsIfPresent(request: Request[_]) = {
    request.headers.get("user-agent")
  }
}

