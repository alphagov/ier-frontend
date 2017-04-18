package uk.gov.gds.ier.transaction.crown.applicationFormVote

import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.{WaysToVoteType}
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.step.StepTemplate
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait PostalOrProxyVoteMustache extends StepTemplate[InprogressCrown] {

  val wayToVote: WaysToVoteType

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
      warning1: Text,
      warning2: Text,
      voteType: Field) extends MustacheData

  val mustache = MustacheTemplate("crown/postalOrProxyVote") {
    (form, postUrl) =>

    implicit val progressForm = form

    val wayToVoteName = wayToVote match {
      case WaysToVoteType.ByPost => "postal"
      case WaysToVoteType.ByProxy => "proxy"
      case _ => ""
    }

    val title = s"Do you want us to send you a $wayToVoteName vote application form?"

      val emailAddress = form(keys.contact.email.detail).value

    PostalOrProxyVoteModel(
      question = Question(
        postUrl = postUrl.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      warning1 = Text (
          value = if (wayToVoteName.equals("postal")) s"It is now too late to apply to vote by post for the elections on 4 May 2017; you can still apply for a proxy vote until " else if
          (wayToVoteName.equals("proxy")) s"Proxy vote application forms need to be received by your Local Electoral Registration Office by" else ""
      ),
      warning2 = Text (
            value = if (wayToVoteName.equals("postal")) s"." else if
            (wayToVoteName.equals("proxy")) s" to be able to vote in the in the election on 4 May 2017." else ""
        ),
      description = Text (
          value = s"If this is your first time using a $wayToVoteName"
           +" vote, or your details have changed, you need to sign"
           +" and return an application form."
      ),
      voteFieldSet = FieldSet(
        classes = if (progressForm(keys.postalOrProxyVote.optIn).hasErrors)
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
        classes = {
          if (progressForm(keys.postalOrProxyVote.deliveryMethod.methodName).hasErrors)
            "invalid"
          else ""
        }
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
        key = keys.postalOrProxyVote.deliveryMethod.emailAddress,
        default = emailAddress
      ),
      voteType =  Field(
        id = keys.postalOrProxyVote.voteType.asId(),
        name = keys.postalOrProxyVote.voteType.key,
        value = wayToVote.name
      )
    )
  }
}
