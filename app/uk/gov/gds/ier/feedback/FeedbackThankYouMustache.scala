package uk.gov.gds.ier.feedback

import uk.gov.gds.ier.step.StepTemplate

trait FeedbackThankYouMustache
  extends StepTemplate[FeedbackRequest] {

  case class FeedbackModel (
      question: Question,
      returnToTransaction: Boolean,
      sourcePath: Field
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
      returnToTransaction = form(keys.sourcePath).value.isDefined,
      sourcePath = HiddenField(
        key = keys.sourcePath,
        value = form(keys.sourcePath).value.getOrElse("")
      )
    )
  }
}
