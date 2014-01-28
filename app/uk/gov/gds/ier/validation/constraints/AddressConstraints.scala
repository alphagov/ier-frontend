package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, NinoValidator}
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.model.{InprogressOrdinary, Address, InprogressApplication, Nino}
import uk.gov.gds.ier.model.{PartialAddress, InprogressApplication, Nino}
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.model.OverseasAddress

trait AddressConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val addressOrManualAddressDefined = Constraint[InprogressOrdinary](keys.address.key) {
    application =>
      application.address match {
        case Some(PartialAddress(_, Some(uprn), _, _)) if !uprn.isEmpty => Valid
        case Some(PartialAddress(_, _, _, Some(manualAddress))) if !manualAddress.isEmpty => Valid
        case _ => Invalid("Please select your address", keys.address.uprn)
      }
  }
  
  
    lazy val overseasAddressCountryDefined = Constraint[Option[OverseasAddress]](keys.overseasAddress.key) {
    overseasAddress =>
        if (overseasAddress.isDefined) Valid
        else Invalid("Please enter your country and address", keys.overseasAddress.country.key, keys.overseasAddress.address)
  }
}
