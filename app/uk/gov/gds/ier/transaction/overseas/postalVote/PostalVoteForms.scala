package uk.gov.gds.ier.transaction.overseas.postalVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressOverseas, PostalVote, PostalVoteDeliveryMethod}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.PostalVoteConstraints

trait PostalVoteForms extends PostalVoteConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val postalVoteDeliveryMethodMapping = mapping(
    keys.methodName.key -> optional(nonEmptyText),
    keys.emailAddress.key -> optional(nonEmptyText)
  )(
    PostalVoteDeliveryMethod.apply
  )(
    PostalVoteDeliveryMethod.unapply
  ) verifying (validDeliveryMethod)

  lazy val postalVoteMapping = mapping(
    keys.optIn.key -> optional(boolean)
      .verifying("Please answer this question", postalVote => postalVote.isDefined),
    keys.deliveryMethod.key -> optional(postalVoteDeliveryMethodMapping)
  ) (
    (postalVoteOption, deliveryMethod) => PostalVote(postalVoteOption, deliveryMethod)
  ) (
    postalVote => Some(postalVote.postalVoteOption, postalVote.deliveryMethod)
  ) verifying (validPostVoteOption)

  val postalVoteForm = ErrorTransformForm(
    mapping(
      keys.postalVote.key -> postalVoteMapping
    ) (
        postalVote => InprogressOverseas (postalVote = Some(postalVote))
    ) (
        inprogress =>  inprogress.postalVote
    )
  )
}

