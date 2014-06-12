package uk.gov.gds.ier.feedback

import com.google.inject.Inject
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.logging.Logging
import play.api.mvc._
import controllers.routes._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.{WithConfig, WithRemoteAssets}

class FeedbackThankYouPage @Inject ()(
    val config: Config,
    val remoteAssets: RemoteAssets)
  extends Controller
  with FeedbackForm
  with FeedbackMustache
  with Logging
  with WithRemoteAssets
  with WithConfig {

  def get(sourcePath: Option[String]) = Action { implicit request =>
    logger.debug(s"GET request for ${request.path}")
    Ok(ThankYouPage(
      sourcePath = sourcePath.getOrElse(config.ordinaryStartUrl)
    ))
  }
}

