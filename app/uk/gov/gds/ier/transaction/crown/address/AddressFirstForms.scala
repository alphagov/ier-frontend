package uk.gov.gds.ier.transaction.crown.address

import play.api.data.Forms._
import uk.gov.gds.ier.model.{LastUkAddress, InprogressCrown}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys, ErrorTransformForm}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}

trait AddressFirstForms extends AddressFirstConstraints {
  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>

  val addressFirstForm = ErrorTransformForm(
    mapping (
      keys.address.hasUkAddress.key -> optional(boolean)
    ) (
      addressYesNo => InprogressCrown(
        address = Some(LastUkAddress(
          hasUkAddress = addressYesNo,
          address = None
        ))
      )
    ) (
      inprogress => Some(inprogress.address.flatMap(_.hasUkAddress))
    ).verifying( addressYesNoIsNotEmpty )
  )
}

trait AddressFirstConstraints extends CommonConstraints {
  self: FormKeys
    with ErrorMessages =>

  lazy val addressYesNoIsNotEmpty = Constraint[InprogressCrown](
    keys.address.hasUkAddress.key) {
    inprogress => inprogress.address match {
      case Some(LastUkAddress(Some(_), _)) => {
        Valid
      }
      case _ => {
        Invalid("Please answer this question", keys.address.hasUkAddress)
      }
    }
  }
}
