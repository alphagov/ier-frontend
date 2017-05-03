package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import uk.gov.gds.ier.model.WaysToVoteType
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.step.StepTemplate

trait PostalOrProxyVoteMustache extends StepTemplate[InprogressOverseas] {

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
      warning3: Text,
      warning4: Text,
      warning5: Text,
      warning6: Text,
      warning7: Text,
      warning8: Text,
      warning9: Text,
      warning10: Text,
      warning11: Text,
      warning12: Text,
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
      warning1 = Text (
        value = if (wayToVoteName.equals("postal")) s"To vote by post in the UK General Election on 8 June 2017, your local Electoral Registration Office must receive your application no later than " else if
        (wayToVoteName.equals("proxy")) s"Proxy vote application forms need to be received by your local Electoral Registration Office no later than  " else ""
      ),
      warning2 = Text (
        value = if (wayToVoteName.equals("postal")) s"5pm on Tuesday 23 May 2017. " else if
        (wayToVoteName.equals("proxy")) s"5pm on 31 May 2017 " else ""
      ),
      warning3 = Text (
        value = if (wayToVoteName.equals("postal")) s"You can apply for a proxy vote until " else if
        (wayToVoteName.equals("proxy")) s"" else ""
      ),
      warning4 = Text (
        value = if (wayToVoteName.equals("postal")) s"5pm on 31 May 2017." else if
        (wayToVoteName.equals("proxy")) s"" else ""
      ),
      warning5 = Text (
        value = if (wayToVoteName.equals("postal")) s"If you are an overseas voter you are strongly advised to vote by proxy, to avoid the risk of postal delays preventing your vote being counted." else if
        (wayToVoteName.equals("proxy")) s"" else ""
      ),
      warning6 = Text (
        value = if (wayToVoteName.equals("postal")) s"" else if
        (wayToVoteName.equals("proxy")) s"Please ensure you allow sufficient time for your proxy vote application to reach your local Electoral Registration Office." else ""
      ),
      warning7 = Text (
        value = if (wayToVoteName.equals("postal")) s"Please allow adequate time for your Electoral Registration Office to process your application and issue your Ballot." else if
        (wayToVoteName.equals("proxy")) s"" else ""
      ),
      warning8 = Text (
        value = if (wayToVoteName.equals("postal")) s"Please be aware that you need to complete a separate application form when you apply for a postal vote.          " else if
        (wayToVoteName.equals("proxy")) s"" else ""
      ),
      warning9 = Text (
        value = if (wayToVoteName.equals("postal")) s"" else if
        (wayToVoteName.equals("proxy")) s"to be able to vote in the UK General Election on 8 June 2017." else ""
      ),
      warning10 = Text (
        value = if (wayToVoteName.equals("postal")) s"You will receive your application sooner if you choose the email option. Your application needs to be printed, signed and returned to your local Electoral Registration Office." else if
        (wayToVoteName.equals("proxy")) s"You will receive your application sooner if you choose the email option. Your application needs to be printed, signed and returned to your local Electoral Registration Office." else ""
      ),
      warning11 = Text (
        value = if (wayToVoteName.equals("postal")) s"To vote by post in the UK General Election on 8 June 2017, your local Electoral Registration Office must receive your application no later than " else if
        (wayToVoteName.equals("proxy")) s"" else ""
      ),
      warning12 = Text (
        value = if (wayToVoteName.equals("postal")) s"5pm on Tuesday 23 May 2017." else if
        (wayToVoteName.equals("proxy")) s"" else ""
      ),
      description = Text (
        value = "If this is your first time using a "+wayToVoteName
          +" vote, or your details have changed, you need to sign and return an application form."
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
