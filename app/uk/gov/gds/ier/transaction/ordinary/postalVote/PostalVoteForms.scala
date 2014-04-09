package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PostalVote, PostalVoteDeliveryMethod}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.PostalVoteConstraints
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PostalVoteForms extends PostalVoteConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val postalVoteForm = ErrorTransformForm(
    mapping(
      keys.postalVote.key -> optional(PostalVote.mapping)
    ) (
      postalVote => InprogressOrdinary(
        postalVote = postalVote
      )
    ) (
      inprogress => Some(
        inprogress.postalVote
      )
    ) verifying (
      validPostVoteOption,
      validEmailAddressIfProvided,
      questionIsAnswered,
      emailProvidedIfEmailAnswered
    )
  )
}

