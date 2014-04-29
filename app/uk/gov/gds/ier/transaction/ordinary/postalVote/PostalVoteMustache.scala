package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PostalVoteMustache extends StepTemplate[InprogressOrdinary] {

  case class PostalVoteModel(
    question: Question,
    postCheckboxYes: Field,
    postCheckboxNo: Field,
    deliveryByEmail: Field,
    deliveryByPost: Field,
    emailField: Field,
    deliveryMethodValid: String
  )

  val mustache = MustacheTemplate("ordinary/postalVote") {
    (form, postUrl) =>

    implicit val progressForm = form

    val emailAddress = form(keys.contact.email.detail).value

    val deliveryMethodValidation =
      if (form(keys.postalVote.deliveryMethod.methodName).hasErrors) "invalid" else ""

    val data = PostalVoteModel(
      question = Question(
        postUrl = postUrl.url,
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
        key = keys.postalVote.deliveryMethod.emailAddress,
        default = emailAddress
      ),
      deliveryMethodValid = deliveryMethodValidation
    )

    MustacheData(data, data.question.title)
  }
}

