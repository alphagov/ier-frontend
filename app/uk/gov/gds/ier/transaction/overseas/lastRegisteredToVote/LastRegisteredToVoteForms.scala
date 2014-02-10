package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, ErrorTransformForm}
import uk.gov.gds.ier.validation.constraints.DateOfBirthConstraints
import play.api.data.Forms._
import scala.Some

trait LastRegisteredToVoteForms {
    self:  FormKeys
      with ErrorMessages =>

  lazy val lastRegisteredToVoteMapping = mapping(
    keys.registeredType.key -> text
  ) (
    registeredTypeParam => LastRegisteredToVote(LastRegisteredType.withName(registeredTypeParam))
  ) (
    registeredTypeObj => Some(registeredTypeObj.lastRegisteredType.toString)
  )

  val lastRegisteredToVoteForm = ErrorTransformForm(
    mapping(
      keys.lastRegisteredToVote.key -> optional(lastRegisteredToVoteMapping)
    ) (
      lastRegisteredToVoteObj => InprogressOverseas(lastRegisteredToVote = lastRegisteredToVoteObj)
    ) (
      inprogress => Some(inprogress.lastRegisteredToVote)
    )
  )
}
