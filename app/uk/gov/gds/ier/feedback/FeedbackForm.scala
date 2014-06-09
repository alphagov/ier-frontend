package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys}
import play.api.data.Forms._

trait FeedbackForm {
  self: FormKeys =>

  val feedbackForm = ErrorTransformForm(
    mapping(
      keys.sourcePath.key -> text,
      keys.feedback.feedbackText.key -> text(0, 1200),
      keys.feedback.contactName.key -> optional(text),
      keys.feedback.contactEmail.key -> optional(text)
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
    )
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

  val feedbackThankYouGetForm = feedbackGetForm

  val feedbackThankYouPostForm = feedbackGetForm
}
