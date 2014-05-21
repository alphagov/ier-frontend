package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.model.OtherAddress
import play.api.data.validation.{Invalid, Valid, Constraint}

trait OtherAddressConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val otherAddressIsValid = Constraint[String](keys.otherAddress.key) {
    addressKind =>
      if (
        addressKind == OtherAddress.NoOtherAddress.name ||
        addressKind == OtherAddress.StudentOtherAddress.name ||
        addressKind == OtherAddress.HomeOtherAddress.name
      ) {
        Valid
      } else {
        Invalid(
          // do not translate, it is not supposed to happen normally
          s"${addressKind} not a valid other address type",
          keys.otherAddress.hasOtherAddress
        )
      }
  }
}
