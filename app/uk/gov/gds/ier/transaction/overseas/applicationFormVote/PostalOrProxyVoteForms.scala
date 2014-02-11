package uk.gov.gds.ier.transaction.overseas.applicationFormVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PostalOrProxyVote, InprogressOverseas, PostalVoteDeliveryMethod}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.overseas.PostalOrProxyVoteConstraints

trait PostalOrProxyVoteForms extends PostalOrProxyVoteConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val voteDeliveryMethodMapping = mapping(
    keys.methodName.key -> optional(nonEmptyText),
    keys.emailAddress.key -> optional(nonEmptyText)
  )(
    PostalVoteDeliveryMethod.apply
  )(
    PostalVoteDeliveryMethod.unapply
  ) verifying (validDeliveryMethod)

  lazy val postalOrProxyVoteMapping = mapping(
    keys.voteType.key -> text,
    keys.optIn.key -> optional(boolean)
      .verifying("Please answer this question", postalVote => postalVote.isDefined),
    keys.deliveryMethod.key -> optional(voteDeliveryMethodMapping)
  ) (
    (voteType, postalVoteOption, deliveryMethod) => PostalOrProxyVote(voteType, postalVoteOption, deliveryMethod)
  ) (
    postalVote => Some(postalVote.typeVote, postalVote.postalVoteOption, postalVote.deliveryMethod)
  ) verifying (validVoteOption)

  val postalOrProxyVoteForm = ErrorTransformForm(
    mapping(
      keys.postalOrProxyVote.key -> postalOrProxyVoteMapping
    ) (
        postalVote => InprogressOverseas (postalOrProxyVote = Some(postalVote))
    ) (
        inprogress =>  inprogress.postalOrProxyVote
    )
  )
}

