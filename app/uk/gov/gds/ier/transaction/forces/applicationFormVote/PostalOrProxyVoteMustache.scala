package uk.gov.gds.ier.transaction.forces.applicationFormVote

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{InprogressForces, WaysToVoteType}
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.mustache.StepMustache

trait PostalOrProxyVoteMustache extends StepMustache {

  case class PostalOrProxyVoteModel(
      question:Question,
      description: Text,
      voteFieldSet: FieldSet,
      voteOptInTrue: Field,
      voteOptInFalse: Field,
      voteDeliveryMethodFieldSet: FieldSet,
      voteDeliveryMethodEmail: Field,
      voteDeliveryMethodPost: Field,
      voteEmailAddress: Field,
      voteType: Field,
      forceRedirectToPostal: Field)

  def transformFormStepToMustacheData (
      form: ErrorTransformForm[InprogressForces],
      postEndpoint: Call,
      backEndpoint: Option[Call],
      wayToVote: WaysToVoteType) : PostalOrProxyVoteModel = {

    implicit val progressForm = form

    val wayToVoteName = wayToVote match {
      case WaysToVoteType.ByPost => "postal"
      case WaysToVoteType.ByProxy => "proxy"
      case _ => ""
    }

    PostalOrProxyVoteModel(
      question = Question(
        postUrl = postEndpoint.url,
        backUrl = backEndpoint.map { call => call.url }.getOrElse(""),
        errorMessages = form.globalErrors.map{ _.message },
        number = "11",
        title = "Do you want us to send you a "+wayToVoteName+" vote application form?"
      ),
      description = Text (
        value = "If this is your first time using a "+wayToVoteName
          +" vote, or your details have changed, you need to sign and return an application form."
      ),
      voteFieldSet = FieldSet(
        classes = if (progressForm(keys.postalOrProxyVote.optIn.key).hasErrors)
          "invalid" else ""
      ),
      voteOptInTrue = RadioField (
        key = keys.postalOrProxyVote.optIn,
        value = "true"
      ),
      voteOptInFalse = RadioField (
        key = keys.postalOrProxyVote.optIn,
        value = "false"
      ),
      voteDeliveryMethodFieldSet = FieldSet(
        classes = if (progressForm(keys.postalOrProxyVote.deliveryMethod.methodName.key).hasErrors)
          "invalid" else ""
      ),
      voteDeliveryMethodEmail = RadioField (
        key = keys.postalOrProxyVote.deliveryMethod.methodName,
        value = "email"
      ),
      voteDeliveryMethodPost = RadioField (
        key = keys.postalOrProxyVote.deliveryMethod.methodName,
        value = "post"
      ),
      voteEmailAddress = TextField (
        key = keys.postalOrProxyVote.deliveryMethod.emailAddress
      ),
      voteType =  Field(
        id = keys.postalOrProxyVote.voteType.asId(),
        name = keys.postalOrProxyVote.voteType.key,
        value = wayToVote.name
      ),
      forceRedirectToPostal =  Field(
        id = keys.forceToRedirect.asId(),
        name = keys.forceToRedirect.key,
        value = form(keys.forceToRedirect.key).value.getOrElse("false")
      )
    )
  }

  def postalOrProxyVoteMustache(
      form:ErrorTransformForm[InprogressForces],
      postEndpoint: Call,
      backEndpoint: Option[Call],
      wayToVote: WaysToVoteType): Html = {

    val data = transformFormStepToMustacheData(form, postEndpoint, backEndpoint, wayToVote)
    val content = Mustache.render("forces/postalOrProxyVote", data)
    MainStepTemplate(content, data.question.title)
  }
}
