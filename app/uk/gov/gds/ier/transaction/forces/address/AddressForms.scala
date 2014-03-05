package uk.gov.gds.ier.transaction.forces.address

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Forms._
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PossibleAddress
import scala.Some
import uk.gov.gds.ier.model.PartialAddress
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation.constraints.AddressConstraints

trait AddressForms extends AddressForcesConstraints {
  self:  FormKeys
    with ErrorMessages
    with WithSerialiser =>

  lazy val possibleAddressMapping = mapping(
    keys.jsonList.key -> nonEmptyText,
    keys.postcode.key -> nonEmptyText
  ) (
    (json, postcode) => PossibleAddress(serialiser.fromJson[Addresses](json), postcode)
  ) (
    possibleAddress => Some(serialiser.toJson(possibleAddress.jsonList), possibleAddress.postcode)
  )

  lazy val partialAddressMapping = mapping(
    keys.addressLine.key -> optional(nonEmptyText),
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText
      .verifying("Please enter a valid postcode", postcode => PostcodeValidator.isValid(postcode)),
    keys.manualAddress.key -> optional(nonEmptyText
      .verifying(addressMaxLengthError, _.size <= maxTextFieldLength))
  ) (
    PartialAddress.apply
  ) (
    PartialAddress.unapply
  )

  val addressForm = ErrorTransformForm(
    mapping(
      keys.address.key -> optional(partialAddressMapping)
        .verifying("Please answer this question", _.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) (
      (partialAddress, possibleAddresses) =>
        InprogressForces(address = partialAddress, possibleAddresses = possibleAddresses)
    ) (
      inprogress => Some(inprogress.address, inprogress.possibleAddresses)
    ) verifying addressOrManualAddressDefinedForces
  )

  val addressLookupForm = ErrorTransformForm(
    mapping(
      keys.possibleAddresses.postcode.key -> text
        .verifying("Please enter a valid postcode", postcode => PostcodeValidator.isValid(postcode))
    ) (
      postcode => InprogressForces(possibleAddresses = Some(PossibleAddress(jsonList = Addresses(List.empty), postcode = postcode)))
    ) (
      inprogress => inprogress.possibleAddresses.map(_.postcode)
    ) 
  )
  
  val addressSizeCheckupForm = ErrorTransformForm(
    mapping(
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) (
      possibleAddresses => InprogressForces(possibleAddresses = possibleAddresses)
    ) (
      inprogress => Some(inprogress.possibleAddresses)
    ) verifying addressLookupForces
  ) 
}

trait AddressForcesConstraints extends AddressConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val addressOrManualAddressDefinedForces = Constraint[InprogressForces](keys.address.key) {
    application =>
      application.address match {
        case Some(PartialAddress(_, Some(uprn), _, _)) if !uprn.isEmpty => Valid
        case Some(PartialAddress(_, _, _, Some(manualAddress))) if !manualAddress.isEmpty => Valid
        case _ => Invalid("Please select your address", keys.address.uprn)
      }
  }

  lazy val addressLookupForces = Constraint[InprogressForces](keys.possibleAddresses.key) {
    application =>
      application.possibleAddresses match {
        case Some(addresses) if (addresses.jsonList.addresses.size > 0) => Valid
        case _ => Invalid("Please enter a valid postcode", keys.possibleAddresses.postcode)
      }
  }
}


