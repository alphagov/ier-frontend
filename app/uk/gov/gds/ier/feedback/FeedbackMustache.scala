package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.mustache.NamedStyleClasses._
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

  case class FeedbackPage (
      pageTitle: String,
      postUrl: String,
      feedbackText: Field,
      contactName: Field,
      contactEmail: Field,
      sourcePath: Field,
      maxFeedbackCommentLength: Int,
      maxFeedbackNameLength: Int,
      maxFeedbackEmailLength: Int,
      feedbackDetailHint: String
  ) (
      implicit val lang: Lang
  ) extends ArticlePage (
    "feedbackForm"
  ) with MessagesForMustache

  object FeedbackPage {
    def apply[T](
        form: ErrorTransformForm[T],
        postUrl: String
    ) (
        implicit lang: Lang
    ) : FeedbackPage = {
      implicit val progressForm = form

      FeedbackPage(
        pageTitle = Messages("feedback_title"),
        postUrl = postUrl,
        feedbackText = TextField(
          key = keys.feedback.feedbackText
        ),
        contactName = TextField(
          key = keys.feedback.contactName
        ),
        contactEmail = TextField(
          key = keys.feedback.contactEmail
        ),
        sourcePath = HiddenField(
          key = keys.sourcePath,
          value = form(keys.sourcePath).value.getOrElse("")
        ),
        maxFeedbackCommentLength = maxFeedbackCommentLength,
        maxFeedbackNameLength = maxFeedbackNameLength,
        maxFeedbackEmailLength = maxFeedbackEmailLength,
        feedbackDetailHint = Messages("feedback_detail_hint", maxFeedbackCommentLength)
      )
    }
  }
}