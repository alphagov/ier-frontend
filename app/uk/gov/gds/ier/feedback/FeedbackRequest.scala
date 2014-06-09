package uk.gov.gds.ier.feedback

case class FeedbackRequest(
  /** URL fragment of page where feedback request originated and we are going to return */
  sourcePath: Option[String],
  comment: String,
  contactName: Option[String],
  contactEmail: Option[String]
)

object FeedbackRequest {
  def apply(): FeedbackRequest = {
    FeedbackRequest(None, "", None, None)
  }
}
