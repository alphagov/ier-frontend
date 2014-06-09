package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys}
import play.api.data.Forms._
import play.api.data.validation._

trait FeedbackForm {
  self: FormKeys =>

  // this is out limit, it works well with Zendesk, not sure what is the real limit
  // also count with some space for appended contact and browser details
  val maxFeedbackCommentLength = 1200

  // another arbitrary limit
  val maxFeedbackNameLength = 100

  // see: http://stackoverflow.com/questions/386294/what-is-the-maximum-length-of-a-valid-email-address
  val maxFeedbackEmailLength = 254

  val feedbackForm = ErrorTransformForm(
    mapping(
      keys.sourcePath.key -> text,
      keys.feedback.feedbackText.key -> text(0, maxFeedbackCommentLength),
      keys.feedback.contactName.key -> optional(text(0, maxFeedbackNameLength)),
      keys.feedback.contactEmail.key -> optional(text(0, maxFeedbackEmailLength))
    ) (
      (sourcePath, comment, contactName, contactEmail) => FeedbackRequest(
        sourcePath = sourcePath,
        comment = comment,
        contactName = contactName,
        contactEmail = contactEmail
      )
    ) (
      request => Some(
        request.sourcePath,
        request.comment,
        request.contactName,
        request.contactName
      )
    ).verifying(feedbackTextCannotBeEmpty)
  )

  val feedbackGetForm = ErrorTransformForm(
    mapping(
      "sourcePath" -> text
    ) (
      (sourcePath) => FeedbackRequest(
        sourcePath = sourcePath,
        comment = "",
        contactName = None,
        contactEmail = None
      )
    ) (
      request => Some(
        request.sourcePath
      )
    )
  )

  lazy val feedbackTextCannotBeEmpty = Constraint[FeedbackRequest] {
    feedbackRequest: FeedbackRequest =>
      if (feedbackRequest.comment.trim.isEmpty) Invalid("Feedback text cannot be empty")
      else Valid
  }

  val feedbackThankYouGetForm = feedbackGetForm

  val feedbackThankYouPostForm = feedbackGetForm
}
