package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, Key}
import uk.gov.gds.ier.model.OtherAddress
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Mapping
import play.api.data.Forms._
import scala.util.{Try, Success, Failure}

trait OtherAddressConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val otherAddressIsValid = Constraint[String](keys.otherAddress.key) {
    str =>
      if (
        str == OtherAddress.NoOtherAddress.name ||
        str == OtherAddress.StudentOtherAddress.name ||
        str == OtherAddress.HomeOtherAddress.name
      ) {
        Valid
      } else {
        Invalid(
          s"$str is not a valid value",
          keys.otherAddress.hasOtherAddress
        )
      }
  }
}
