package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.PreviousAddress

trait PreviousAddressConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val addressExistsIfMovedRecently = Constraint[PreviousAddress](keys.previousAddress.previousAddress.key) {
    previousAddress =>
      if (!previousAddress.movedRecently ||
        (previousAddress.movedRecently && previousAddress.previousAddress.isDefined)) Valid
      else Invalid("Please enter your postcode", keys.previousAddress.previousAddress.postcode)
  }

  lazy val movedRecentlyTrueIfAddressProvided = Constraint[PreviousAddress](keys.previousAddress.movedRecently.key) {
    previousAddress =>
      if (previousAddress.movedRecently ||
        (!previousAddress.movedRecently && previousAddress.previousAddress.isEmpty)) Valid
      else Invalid("Please answer this question", keys.previousAddress.movedRecently)
  }

}