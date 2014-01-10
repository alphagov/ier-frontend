package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, NinoValidator}
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.model.{InprogressOrdinary, Address, InprogressApplication, Nino}

trait AddressConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val addressOrManualAddressDefined = Constraint[InprogressOrdinary](keys.address.key) {
    application =>
      application.address match {
        case Some(Address(Some(addressLine), _, _)) => Valid
        case Some(Address(_, _, Some(manualAddress))) => Valid
        case _ => Invalid("Please select your address", keys.address.address)
      }
  }
}
