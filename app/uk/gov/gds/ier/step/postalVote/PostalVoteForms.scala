package uk.gov.gds.ier.step.postalVote

import uk.gov.gds.ier.validation.{TransformedForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.data.Form
import play.api.data.Forms._

trait PostalVoteForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val postalVoteOptInMapping = single(
    keys.optIn.key -> boolean
  )
 
  val postalVoteForm = TransformedForm(
    mapping(
      keys.postalVote.key -> optional(postalVoteOptInMapping)
        .verifying("Please answer this question", postalVote => postalVote.isDefined)
    ) (
      postalVote => InprogressApplication(postalVoteOptin = postalVote)
    ) (
      inprogress => Some(inprogress.postalVoteOptin)
    )
  )
}

