package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.mustache.StepMustache
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Call
import play.api.templates.Html

trait PostalVoteMustache extends StepMustache {

  case class PostalVoteModel(
    question: Question,
    postCheckboxYes: Field,
    postCheckboxNo: Field,
    deliveryByEmail: Field,
    deliveryByPost: Field,
    emailField: Field
  )

  def transformFormStepToMustacheData(
      form: ErrorTransformForm[InprogressOrdinary],
      postUrl: Call,
      backUrl: Option[Call]): PostalVoteModel = {
    implicit val progressForm = form

    PostalVoteModel(
      question = Question(
        postUrl = postUrl.url,
        backUrl = backUrl.map { call => call.url }.getOrElse(""),
        showBackUrl = backUrl.isDefined,
        number = "10",
        title = "Do you want to apply for a postal vote?",
        errorMessages = form.globalErrors.map { _.message }),
      postCheckboxYes = RadioField(
        key = keys.postalVote.optIn,
        value = "true"),
      postCheckboxNo = RadioField(
        key = keys.postalVote.optIn,
        value = "false"),
      deliveryByEmail = RadioField(
        key = keys.postalVote.deliveryMethod.methodName,
        value = "email"),
      deliveryByPost = RadioField(
        key = keys.postalVote.deliveryMethod.methodName,
        value = "post"),
      emailField = TextField(
        key = keys.postalVote.deliveryMethod.emailAddress)
    )
  }

  def postalVoteMustache(
      form: ErrorTransformForm[InprogressOrdinary],
      call: Call, backUrl: Option[Call]): Html = {
    val data = transformFormStepToMustacheData(form, call, backUrl)
    val content = Mustache.render("ordinary/postalVote", data)
    MainStepTemplate(content, data.question.title)
  }
}
