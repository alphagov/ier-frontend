package uk.gov.gds.ier.transaction.overseas.waysToVote

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html

trait WaysToVoteMustache extends StepMustache {

  val pageTitle = "How do you want to vote?"

  case class WaysToVoteModel(
    question: Question,
    byPost: Field,
    byProxy: Field,
    inPerson: Field
  )

  def transformFormStepToMustacheData(
      form: ErrorTransformForm[InprogressOverseas],
      postUrl: String,
      backUrl: Option[String]): WaysToVoteModel = {
    implicit val progressForm = form

    WaysToVoteModel(
      question = Question(
        postUrl = postUrl,
        backUrl = backUrl.getOrElse(""),
        showBackUrl = backUrl.isDefined,
        number = "12",
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      byPost = RadioField(
        key = keys.waysToVote.wayType,
        value = "by-post"),
      byProxy = RadioField(
        key = keys.waysToVote.wayType,
        value = "by-proxy"),
      inPerson = RadioField(
        key = keys.waysToVote.wayType,
        value = "in-person")
    )
  }

  def waysToVoteMustache(
      form: ErrorTransformForm[InprogressOverseas],
      call: Call, backUrl: Option[String]): Html = {
    val data = transformFormStepToMustacheData(form, call.url, backUrl)
    val content = Mustache.render("overseas/waysToVote", data)
    MainStepTemplate(content, pageTitle)
  }
}
