package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{Address, PreviousAddress}

trait PreviousAddressConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val addressExistsIfMovedRecently = Constraint[PreviousAddress](keys.previousAddress.previousAddress.key) {
    previousAddress =>
        previousAddress match {
          case PreviousAddress(false, _) => Valid
          case PreviousAddress(true, Some(Address(Some(addressLine), _, _))) => Valid
          case PreviousAddress(true, Some(Address(_, _, Some(manualAddress)))) => Valid
          case _ => Invalid("Please select your address", keys.previousAddress.previousAddress.address)
        }
  }

  lazy val movedRecentlyTrueIfAddressProvided = Constraint[PreviousAddress](keys.previousAddress.movedRecently.key) {
    previousAddress =>
      if (previousAddress.movedRecently ||
        (!previousAddress.movedRecently && previousAddress.previousAddress.isEmpty)) Valid
      else Invalid("Please answer this question", keys.previousAddress.movedRecently)
  }

}