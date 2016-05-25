package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import uk.gov.gds.ier.model.WaysToVoteType
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait PostalOrProxyVoteMustache extends StepTemplate[InprogressOverseas] {

  val wayToVote: WaysToVoteType

  case class PostalOrProxyVoteModel(
      question:Question,
      description: Text,
      warning: Text,
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

    val postalOrProxy = wayToVote match {
      case WaysToVoteType.ByPost => "post"
      case WaysToVoteType.ByProxy => "proxy"
      case _ => ""
    }

    val date = wayToVote match {
      case WaysToVoteType.ByPost => "Wednesday 8 June"
      case WaysToVoteType.ByProxy => "Wednesday 15 June"
      case _ => ""
    }

    val postalTiming = wayToVote match {
      case WaysToVoteType.ByPost => "Delivery timing for ballot packs overseas can’t be guaranteed. With the deadline so close, consider voting by proxy instead."
      case WaysToVoteType.ByProxy => ""
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
      warning = Text (
        value = s"To vote by $postalOrProxy"
          +" in the EU referendum on the 23 June, your " + wayToVoteName + " vote application must reach your local Electoral Registration Office by"
      ),
      warning2 = Text (
        value = " 5pm on "
          +s"$date."
      ),
      warning3 = Text (
        value = s"$postalTiming"
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
