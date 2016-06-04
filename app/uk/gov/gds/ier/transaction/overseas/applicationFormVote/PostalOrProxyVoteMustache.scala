package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import uk.gov.gds.ier.model.WaysToVoteType
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait PostalOrProxyVoteMustache extends StepTemplate[InprogressOverseas] {

  val wayToVote: WaysToVoteType

  case class PostalOrProxyVoteModel(
      question:Question,
      description: Text,
      warning1: Text,
      warning2: Text,
      warning3: Text,
      voteFieldSet: FieldSet,
      voteOptInTrue: Field,
      voteOptInFalse: Field,
      voteDeliveryMethodFieldSet: FieldSet,
      voteDeliveryMethodEmail: Field,
      voteDeliveryMethodPost: Field,
      voteEmailAddress: Field,
      voteType: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("overseas/postalOrProxyVote") { (form, post) =>

    implicit val progressForm = form

    val emailAddress = form(keys.contact.email.detail).value

    val wayToVoteName = wayToVote match {
      case WaysToVoteType.ByPost => "postal"
      case WaysToVoteType.ByProxy => "proxy"
      case _ => ""
    }

    val title = "Do you want us to send you a "+wayToVoteName+" vote application form?"

    PostalOrProxyVoteModel(
      question = Question(
        postUrl = post.url,
        errorMessages = form.globalErrors.map{ _.message },
        title = title
      ),
      description = Text (
        value = "If this is your first time using a "+wayToVoteName
          +" vote, or your details have changed, you need to sign and return an application form."
      ),
      warning1 = Text (
        value = if (wayToVoteName.equals("postal")) s"It is now too late to submit your postal vote application for the EU referendum on 23 June." else if
        (wayToVoteName.equals("proxy")) s"To vote by proxy in the EU referendum on the 23 June, your proxy vote application must reach your local Electoral Registration Office by " else ""
      ),
      warning2 = Text (
        value = if (wayToVoteName.equals("proxy")) s"5pm on Wednesday 15 June." else ""
      ),
      warning3 = Text (
        value = if (wayToVoteName.equals("postal")) s"You can still apply for a proxy vote for the EU referendum until the 15 June." else ""
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
        classes = if (progressForm(keys.postalOrProxyVote.deliveryMethod.methodName).hasErrors)
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
