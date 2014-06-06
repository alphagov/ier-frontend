package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.step.StepTemplate

trait FeedbackThankYouMustache
  extends StepTemplate[FeedbackRequest] {

  case class FeedbackModel (
      question: Question,
      returnToProgress: Boolean
  ) extends MustacheData

  val mustache = MultilingualTemplate("feedbackThankYou") { implicit lang => (form, post) =>
    implicit val progressForm = form

    FeedbackModel(
      question = Question(
        postUrl = post.url,
        errorMessages = Nil,
        number = "",
        title = Messages("feedback_thankYou_title")
      ),
      returnToProgress = !post.url.isEmpty
    )
  }
}
