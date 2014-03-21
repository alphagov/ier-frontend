package uk.gov.gds.ier.transaction.crown.previousAddress

import play.api.data.Forms._
import uk.gov.gds.ier.model.{InprogressCrown, PartialPreviousAddress}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys, ErrorTransformForm}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}

trait PreviousAddressFirstForms extends PreviousAddressFirstConstraints {
  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>

  val previousAddressFirstForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.movedRecently.key -> optional(boolean)
    ) (
      previousAddressYesNo => InprogressCrown(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = previousAddressYesNo,
          previousAddress = None
        ))
      )
    ) (
      inprogress => Some(inprogress.previousAddress.flatMap(_.movedRecently))
    ).verifying( previousAddressYesNoIsNotEmpty )
  )
}

trait PreviousAddressFirstConstraints extends CommonConstraints {
  self: FormKeys
    with ErrorMessages =>

  lazy val previousAddressYesNoIsNotEmpty = Constraint[InprogressCrown](
    keys.previousAddress.movedRecently.key) {
    inprogress => inprogress.previousAddress match {
      case Some(PartialPreviousAddress(Some(_), _)) => {
        Valid
      }
      case _ => {
        Invalid("Please answer this question", keys.previousAddress.movedRecently)
      }
    }
  }
}
