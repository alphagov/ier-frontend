package uk.gov.gds.ier.validation.constraints.overseas

import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{WaysToVote}

trait WaysToVoteConstraints extends CommonConstraints {
  self:  FormKeys
    with ErrorMessages =>

// TODO: refactor and move validations from forms here
//  lazy val validWayToVoteOption = Constraint[Option[WaysToVote]](keys.waysToVote.key) {
//    waysToVote =>
//      if (waysToVote.isDefined) Valid
//      else Invalid("Please select your preferred way to vote", keys.waysToVote)
//  }
}
