package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PostalVoteDeliveryMethod, InprogressOrdinary, InprogressApplication}
import play.api.data.Form
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


  val postalVoteForm = ErrorTransformForm(
    mapping(
      keys.postalVote.optIn.key -> optional(boolean)
        .verifying("Please answer this question", postalVote => postalVote.isDefined),
      keys.deliveryMethod.key -> optional(postalVoteDeliveryMethodMapping)
    ) (
        (postalVote, deliveryMethod) =>
          InprogressOrdinary(
            postalVoteOptin = postalVote,
            postalVoteDeliveryMethod = deliveryMethod)
    ) (
        inprogress =>
          Some(
            inprogress.postalVoteOptin,
            inprogress.postalVoteDeliveryMethod
          )
    ) verifying (validPostVoteOption)
  )
}

