package uk.gov.gds.ier.feedback

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.assets.RemoteAssets
import controllers.routes.FeedbackController
import controllers.routes.FeedbackThankYouController
import uk.gov.gds.ier.logging.Logging
import play.api.mvc._

class FeedbackPage @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val feedbackClient: FeedbackClient
) extends Controller
  with FeedbackForm
  with FeedbackMustache
  with Logging
{

  val validation = feedbackForm

  val postRoute = FeedbackController.post

  def get() = Action { implicit request =>
    logger.debug(s"GET request for ${request.path}")
    val form = feedbackGetForm.bindFromRequest()
    val sourcePath = form(keys.sourcePath).value.getOrElse("")
    logger.debug(s"FeedbackPage source path ${sourcePath}")
    Ok(mustache(
      form,
      postRoute,
      FeedbackRequest()
    ).html)
  }

  def post() = Action { implicit request =>
    logger.debug(s"POST request for ${request.path}")
    validation.bindFromRequest().fold(
      hasErrors => {
        logger.debug(s"Form binding error: ${hasErrors}")
        val sourcePath = hasErrors(keys.sourcePath).value
        Redirect(FeedbackThankYouController.get(sourcePath.getOrElse("")))
      },
      success => {
        logger.debug(s"Form binding successful, proceed with submitting feedback")
        feedbackClient.submit(
          FeedbackSubmissionData(List(
            success.comment,
            success.contactName map {
              name => s"contact name ${name}"} getOrElse("No contact name provided"),
            success.contactEmail map {
              email => s"contact email ${email}"} getOrElse("No contact email provided")
          ).mkString("\n")))
        Redirect(FeedbackThankYouController.get(success.sourcePath))
      }
    )
  }
}

