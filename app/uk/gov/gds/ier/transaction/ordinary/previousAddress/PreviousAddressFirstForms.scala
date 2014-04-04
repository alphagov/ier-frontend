package uk.gov.gds.ier.transaction.ordinary.previousAddress

import play.api.data.Forms._
import uk.gov.gds.ier.model.{PartialAddress, PartialPreviousAddress}
import uk.gov.gds.ier.validation.{PostcodeValidator, ErrorMessages, FormKeys, ErrorTransformForm}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressFirstForms
    extends PreviousAddressFirstConstraints
    with CommonForms {
  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>

  val previousAddressFirstForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(PartialPreviousAddress.mapping)
    ) (
      previousAddressYesNo => InprogressOrdinary(
        previousAddress = previousAddressYesNo
      )
    ) (
      inprogress => Some(inprogress.previousAddress)
    ).verifying( previousAddressYesNoIsNotEmpty )
  )
}

trait PreviousAddressFirstConstraints extends CommonConstraints {
  self: FormKeys
    with ErrorMessages =>

  lazy val previousAddressYesNoIsNotEmpty = Constraint[InprogressOrdinary](
    keys.previousAddress.movedRecently.key) {
    inprogress => inprogress.previousAddress match {
      case Some(PartialPreviousAddress(Some(_), _)) => Valid
      case _ => Invalid(
        "Please answer this question",
        keys.previousAddress.movedRecently
      )
    }
  }
}
