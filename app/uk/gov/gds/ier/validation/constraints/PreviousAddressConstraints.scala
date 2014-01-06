package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{PartialAddress, PartialPreviousAddress}

trait PreviousAddressConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val addressExistsIfMovedRecently = Constraint[PartialPreviousAddress](keys.previousAddress.previousAddress.key) {
    previousAddress =>
      previousAddress match {
        case PartialPreviousAddress(false, _) => Valid
        case PartialPreviousAddress(true, Some(PartialAddress(_, _, Some(manualAddress)))) => Valid  
        case PartialPreviousAddress(true, Some(PartialAddress(Some(uprn), _, _))) => Valid
        case _ => Invalid("Please select your address", keys.previousAddress.previousAddress.uprn)
      }
  }

  lazy val movedRecentlyTrueIfAddressProvided = Constraint[PartialPreviousAddress](keys.previousAddress.movedRecently.key) {
    previousAddress =>
      if (previousAddress.movedRecently ||
        (!previousAddress.movedRecently && previousAddress.previousAddress.isEmpty)) Valid
      else Invalid("Please answer this question", keys.previousAddress.movedRecently)
  }
}
