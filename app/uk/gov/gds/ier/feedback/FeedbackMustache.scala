package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.step.StepTemplate

trait FeedbackMustache
  extends StepTemplate[FeedbackRequest] {

  case class FeedbackModel (
      question: Question,
      feedbackText: Field,
      contactName: Field,
      contactEmail: Field,
      sourcePath: Field
  ) extends MustacheData

  val mustache = MultilingualTemplate("feedbackForm") { implicit lang => (form, post) =>
    implicit val progressForm = form

    FeedbackModel(
      question = Question(
        postUrl = post.url,
        errorMessages = Nil,
        number = "",
        title = Messages("feedback_title")
      ),
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
        key = keys.feedback.sourcePath,
        value = form(keys.sourcePath).value.getOrElse("")
      )
    )
  }
}
