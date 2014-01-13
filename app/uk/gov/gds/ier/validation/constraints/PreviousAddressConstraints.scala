package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{PostcodeValidator, FormKeys, ErrorMessages}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{InprogressApplication, PartialAddress, PartialPreviousAddress}

trait PreviousAddressConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val previousAddressValidations = Constraint[InprogressApplication](keys.previousAddress.previousAddress.key) {
    inprogressApplication =>
      if (inprogressApplication.previousAddress.isDefined) {
        inprogressApplication.previousAddress.get match {
          case PartialPreviousAddress(None,_,_) => Invalid("Please answer this question", keys.previousAddress.movedRecently)
          case PartialPreviousAddress(Some(false),true, _) => Invalid("Please click 'Continue' if you haven't moved", keys.previousAddress.movedRecently)
          case PartialPreviousAddress(Some(true), _, None) => Invalid("Please enter the postcode and click 'Find address'", keys.previousAddress.previousAddress.postcode)
          case PartialPreviousAddress(Some(true), false, Some(PartialAddress(None, None, postcode, None))) => {
            if (!inprogressApplication.possibleAddresses.isDefined) {
              Invalid("Please enter the postcode and click 'Find address'", keys.previousAddress.previousAddress.postcode)
            }
            else {
              Invalid("Please select your address", keys.previousAddress.previousAddress.uprn)
            }
          }
          case PartialPreviousAddress(Some(true), true, Some(PartialAddress(_, _, postcode, _))) => {
            if (!PostcodeValidator.isValid(postcode)) {
              Invalid("This postcode is not valid", keys.previousAddress.previousAddress.postcode)
            }
            else Valid
          }
          case _ => Valid
        }
      }
      else Invalid("Please answer this question", keys.previousAddress.movedRecently)
  }
}
