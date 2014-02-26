package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Call
import play.api.templates.Html

trait PostalVoteMustache extends StepMustache {

  val pageTitle = "Do you want to apply for a postal vote?"

  case class PostalVoteModel(
    question: Question,
    byPostYes: Field,
    byPostNo: Field
  )

  def transformFormStepToMustacheData(
      form: ErrorTransformForm[InprogressOrdinary],
      postUrl: String,
      backUrl: Option[String]): PostalVoteModel = {
    implicit val progressForm = form

    PostalVoteModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        showBackUrl = backUrl.isDefined,
        number = "10",
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      byPostYes = RadioField(
        key = keys.postalVote,
        value = "by-post-yes"),
      byPostNo = RadioField(
        key = keys.postalVote,
        value = "by-post-no")
    )
  }

  def postalVoteMustache(
      form: ErrorTransformForm[InprogressOrdinary],
      call: Call, backUrl: Option[String]): Html = {
    val data = transformFormStepToMustacheData(form, call.url, backUrl)
    val content = Mustache.render("ordinary/postalVote", data)
    MainStepTemplate(content, pageTitle)
  }
}
