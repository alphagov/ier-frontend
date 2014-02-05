package uk.gov.gds.ier.transaction.overseas.waysToVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.model.{WaysToVoteType, WaysToVote, InprogressOverseas}
import uk.gov.gds.ier.validation.constraints.overseas.WaysToVoteConstraints

trait WaysToVoteForms extends WaysToVoteConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val waysToVoteMapping = mapping(
    keys.waysToVote.key -> text
  ) (
    wayToVoteAsString => WaysToVote(WaysToVoteType.withName(wayToVoteAsString))
  ) (
    wayToVoteAsObj => Some(wayToVoteAsObj.waysToVoteType.toString)
  )

  val waysToVoteForm = ErrorTransformForm(
    mapping(
      keys.waysToVote.key -> optional(waysToVoteMapping)
    ) (
      waysToVote => InprogressOverseas(waysToVote = waysToVote)
    ) (
      inprogressApplication => Some(inprogressApplication.waysToVote)
    )
  )
}
