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
    val feedbackClient: FeedbackClient)
  extends Controller
  with FeedbackForm
  with FeedbackMustache
  with Logging {

  val validation = feedbackForm

  val postRoute = FeedbackController.post

  val fixedTicketSubject = "ier-frontend-feedback page"

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
        val browserDetails = getBrowserAndOsDetailsIfPresent(request)
        feedbackClient.submit(
          FeedbackSubmissionData(
            fixedTicketSubject,
            fudgeTicketBodyText(success, browserDetails)
          )
        )
        Redirect(FeedbackThankYouController.get(success.sourcePath))
      }
    )
  }

  private[feedback] def getBrowserAndOsDetailsIfPresent(request: Request[_]) = {
    request.headers.get("user-agent")
  }

  val separatorBetweenCommentAndAppendedFields = "\n"

  /**
   * Append contact and browser details to a ticket body text as there are no proper fields for
   * them in Zendesk API and it is common practice
   */
  private[feedback] def fudgeTicketBodyText(
      request: FeedbackRequest,
      browserDetails: Option[String]) = {
    List(
      request.comment,
      separatorBetweenCommentAndAppendedFields,
      request.contactName map {
        name => s"Contact name: ${name}"} getOrElse("No contact name was provided"),
      request.contactEmail map {
        email => s"Contact email: ${email}"} getOrElse("No contact email was provided"),
      browserDetails map {
        details => s"Browser details: ${details}"} getOrElse("No browser details were provided")
    ).mkString("\n")
  }
}

