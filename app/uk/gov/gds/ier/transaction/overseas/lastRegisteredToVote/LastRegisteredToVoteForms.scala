package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, ErrorTransformForm}
import uk.gov.gds.ier.validation.constraints.overseas.LastRegisteredToVoteConstraints
import play.api.data.Forms._

trait LastRegisteredToVoteForms extends LastRegisteredToVoteConstraints {
  self: FormKeys 
  with ErrorMessages =>

  lazy val lastRegisteredToVoteMapping = mapping(
    keys.registeredType.key -> text.verifying(registeredTypeIsValid)
  ) (
    registeredTypeParam => LastRegisteredToVote(
      LastRegisteredType.parse(registeredTypeParam)
    )
  ) (
    registeredTypeObj => Some(registeredTypeObj.lastRegisteredType.name)
  )

  val lastRegisteredToVoteForm = ErrorTransformForm(
    mapping(
      keys.lastRegisteredToVote.key -> optional(lastRegisteredToVoteMapping)
    ) (
      lastRegisteredToVoteObj => InprogressOverseas(
        lastRegisteredToVote = lastRegisteredToVoteObj
      )
    ) (
      inprogress => Some(inprogress.lastRegisteredToVote)
    ).verifying(lastRegisteredToVoteRequired)
  )
}
