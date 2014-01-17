package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{PostcodeValidator, FormKeys, ErrorMessages}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{PossibleAddress, InprogressOrdinary, PartialAddress, PartialPreviousAddress}

trait PreviousAddressConstraints {
  self: ErrorMessages
    with FormKeys =>

    lazy val previousAddressValidations = Constraint[InprogressOrdinary](keys.previousAddress.previousAddress.key) {
      inprogressOrdinary =>
        if (inprogressOrdinary.previousAddress.isDefined) {
          inprogressOrdinary.previousAddress.get match {
            case PartialPreviousAddress(None, _, _) => Invalid("Please answer this question", keys.previousAddress.movedRecently)
            case PartialPreviousAddress(Some(false), findAddress, previousAddress) => validateNotMoved(findAddress, previousAddress)
            case PartialPreviousAddress(Some(true), findAddress, previousAddress) => validateMoved(inprogressOrdinary.possibleAddresses,findAddress, previousAddress)
            case _ => Valid
          }
        }
        else Invalid("Please answer this question", keys.previousAddress.movedRecently)
    }


    def validateNotMoved (findAddress:Boolean, previousAddress:Option[PartialAddress]) = {
      if (findAddress)
        Invalid("Please click 'Continue' if you haven't moved", keys.previousAddress.movedRecently)
      else
        Valid
    }

    def validateMoved (possibleAddresses:Option[PossibleAddress], findAddress:Boolean, previousAddress:Option[PartialAddress]) = {
      if (previousAddress == None)
        Invalid("Please enter the postcode and click 'Find address'", keys.previousAddress.previousAddress.postcode)
      else
         if (findAddress)
           previousAddress match {
             case Some(PartialAddress(_, _, postcode, _)) => {
               if (!PostcodeValidator.isValid(postcode))
                 Invalid("This postcode is not valid", keys.previousAddress.previousAddress.postcode)
               else Valid
             }
             case _ => Valid
           }
         else
           previousAddress match {
             case Some(PartialAddress(None, None, postcode, None)) =>
               if (!possibleAddresses.isDefined)
                 Invalid("Please enter the postcode and click 'Find address'", keys.previousAddress.previousAddress.postcode)
               else
                 Invalid("Please select your address", keys.previousAddress.previousAddress.uprn)
             case _ => Valid
           }
    }

}
