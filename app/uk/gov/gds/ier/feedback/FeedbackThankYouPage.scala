package uk.gov.gds.ier.feedback

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.logging.Logging
import play.api.mvc._

class FeedbackThankYouPage @Inject ()(
  val serialiser: JsonSerialiser,
  val config: Config,
  val encryptionService: EncryptionService,
  val remoteAssets: RemoteAssets
) extends Controller
  with FeedbackForm
  with FeedbackThankYouMustache
  with Logging {

  def get(sourcePath: String) = Action { implicit request =>
    logger.debug(s"GET request for ${request.path}")
    val form = feedbackGetForm.bindFromRequest()
    val sourcePath = form(keys.sourcePath).value.getOrElse("")
    val postRoute = Call("POST", sourcePath)
    Ok(mustache(
      form,
      postRoute,
      FeedbackRequest()
    ).html)
  }
}

