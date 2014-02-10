package uk.gov.gds.ier.transaction.overseas.waysToVote

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.model.{WaysToVoteType, WaysToVote, InprogressOverseas}

trait WaysToVoteForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val waysToVoteMapping = mapping(
    keys.wayType.key -> text.verifying("Unknown type",
      r => WaysToVoteType.values.exists(_.toString == r))
  ) (
    wayToVoteAsString => WaysToVote(WaysToVoteType.withName(wayToVoteAsString))
  ) (
    wayToVoteAsObj => Some(wayToVoteAsObj.waysToVoteType.toString)
  )

  val waysToVoteForm = ErrorTransformForm(
    mapping(
      keys.waysToVote.key -> optional(waysToVoteMapping)
        .verifying("Please answer this question", waysToVote => waysToVote.isDefined)
    ) (
      waysToVote => InprogressOverseas(waysToVote = waysToVote)
    ) (
      inprogressApplication => Some(inprogressApplication.waysToVote)
    )
  )
}