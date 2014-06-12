package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.mustache.{MustacheModel, StepMustache}
import uk.gov.gds.ier.guice.{WithConfig, WithRemoteAssets}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.langs.Messages

trait FeedbackMustache
  extends StepMustache
  with MustacheModel {
    self: WithRemoteAssets
     with FeedbackForm
     with WithConfig =>

  case class ThankYouPage (
      sourcePath: String = "",
      pageTitle: String = Messages("feedback_thankYou_title")
  ) (
      implicit val lang: Lang
  ) extends ArticlePage("feedbackThankYou")
    with MessagesForMustache

  private[this] implicit val progressForm = ErrorTransformForm(feedbackForm)

  case class FeedbackPage (
      pageTitle: String = Messages("feedback_title"),
      postUrl: String,
      feedbackText: Field = TextField(
        key = keys.feedback.feedbackText
      ),
      contactName: Field = TextField(
        key = keys.feedback.contactName
      ),
      contactEmail: Field = TextField(
        key = keys.feedback.contactEmail
      ),
      maxFeedbackCommentLength: Int = maxFeedbackCommentLength,
      maxFeedbackNameLength: Int = maxFeedbackNameLength,
      maxFeedbackEmailLength: Int = maxFeedbackEmailLength,
      feedbackDetailHint: String = Messages(
        "feedback_detail_hint",
        maxFeedbackCommentLength
      )
  ) (
      implicit val lang: Lang
  ) extends ArticlePage("feedbackForm")
    with MessagesForMustache
}