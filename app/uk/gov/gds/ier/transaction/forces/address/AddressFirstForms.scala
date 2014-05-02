package uk.gov.gds.ier.transaction.forces.address

import play.api.data.Forms._
import uk.gov.gds.ier.model.{LastUkAddress}
import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys, ErrorTransformForm}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait AddressFirstForms extends AddressFirstConstraints {
  self: FormKeys
    with AddressForms
    with ErrorMessages
    with WithSerialiser =>

  val addressFirstForm = ErrorTransformForm(
    addressForm.mapping.verifying(addressYesNoIsNotEmpty)
  )
}

trait AddressFirstConstraints extends CommonConstraints {
  self: FormKeys
    with ErrorMessages =>

  lazy val addressYesNoIsNotEmpty = Constraint[InprogressForces](
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
