package uk.gov.gds.ier.transaction.overseas.postalVote

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.InprogressOverseas
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait PostalVoteMustache extends StepMustache {

  case class PostalVoteModel(question:Question,
                             description: Text,
                             postalVoteOptInTrue: Field,
                             postalVoteOptInFalse: Field,
                             postalVoteDeliveryMethodEmail: Field,
                             postalVoteDeliveryMethodPost: Field,
                             postalVoteEmailAddress: Field )

  def transformFormStepToMustacheData(form: ErrorTransformForm[InprogressOverseas], postEndpoint: Call, backEndpoint: Option[Call]) : PostalVoteModel = {
    implicit val progressForm = form

    // TODO: get proxy or postal!
    val wayToVote = form.value.map(application => application.waysToVote.map(waysToVote => waysToVote.toString).getOrElse("")).getOrElse("")

    PostalVoteModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "11",
        title = "Do you want us to send you a "+wayToVote+" vote application form?"
      ),
      description = Text (
        value = "If this is your first time using a "+wayToVote+" vote, or your details have changed, you need to sign and return an application form."
      ),
      postalVoteOptInTrue = RadioField (
        key = keys.postalVote.optIn,
        value = "true"
      ),
      postalVoteOptInFalse = RadioField (
        key = keys.postalVote.optIn,
        value = "false"
      ),
      postalVoteDeliveryMethodEmail = RadioField (
        key = keys.postalVote.deliveryMethod.methodName,
        value = "email"
      ),
      postalVoteDeliveryMethodPost = RadioField (
        key = keys.postalVote.deliveryMethod.methodName,
        value = "post"
      ),
      postalVoteEmailAddress = TextField (
        key = keys.postalVote.deliveryMethod.emailAddress
      )
    )
  }

  def postalVoteMustache(form:ErrorTransformForm[InprogressOverseas], postEndpoint: Call, backEndpoint: Option[Call]): Html = {
    val data = transformFormStepToMustacheData(form, postEndpoint, backEndpoint)
    val content = Mustache.render("overseas/postalVote", data)
    MainStepTemplate(content, data.question.title)
  }
}
