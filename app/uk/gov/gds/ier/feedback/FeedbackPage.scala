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

  val postRoute = FeedbackController.post

  def get() = Action { implicit request =>
    logger.debug(s"GET request for ${request.path}")
    val form = feedbackGetForm.bindFromRequest()
    val sourcePath = form(keys.sourcePath).value.getOrElse("")
    logger.debug(s"FeedbackPage source path ${sourcePath}")
    Ok(FeedbackPage(form, postRoute.url))
  }

  def post() = Action { implicit request =>
    logger.debug(s"POST request for ${request.path}")
    validation.bindFromRequest().fold(
      hasErrors => {
        logger.debug(s"Form binding error: ${hasErrors}")
        val sourcePath = hasErrors(keys.sourcePath).value
        Redirect(FeedbackController.thankYou(sourcePath))
      },
      success => {
        logger.debug(s"Form binding successful, proceed with submitting feedback")
        val browserDetails = getBrowserAndOsDetailsIfPresent(request)
        feedbackService.submit(success, browserDetails)
        Redirect(FeedbackController.thankYou(success.sourcePath))
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

