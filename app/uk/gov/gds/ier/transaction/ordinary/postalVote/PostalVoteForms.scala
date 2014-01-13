package uk.gov.gds.ier.transaction.ordinary.postalVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import play.api.data.Form
import play.api.data.Forms._

trait PostalVoteForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val postalVoteOptInMapping = single(
    keys.optIn.key -> boolean
  )
 
  val postalVoteForm = ErrorTransformForm(
    mapping(
      keys.postalVote.key -> optional(postalVoteOptInMapping)
        .verifying("Please answer this question", postalVote => postalVote.isDefined)
    ) (
      postalVote => InprogressOrdinary(postalVoteOptin = postalVote)
    ) (
      inprogress => Some(inprogress.postalVoteOptin)
    )
  )
}

