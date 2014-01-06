package uk.gov.gds.ier.step.address

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.model.{InprogressApplication, Address, Addresses, PossibleAddress}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.model.PossibleAddress
import scala.Some
import uk.gov.gds.ier.model.Address
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.validation.constraints.AddressConstraints

trait AddressForms extends AddressConstraints {
  self:  FormKeys
    with ErrorMessages
    with WithSerialiser =>

  lazy val possibleAddressMapping = mapping(
    keys.jsonList.key -> nonEmptyText,
    keys.postcode.key -> nonEmptyText
  ) (
    (json, postcode) => PossibleAddress(serialiser.fromJson[Addresses](json).addresses, postcode)
  ) (
    possibleAddress => Some(serialiser.toJson(possibleAddress.addresses), possibleAddress.postcode)
  )

  lazy val partialAddressMapping = mapping(
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText
      .verifying("Your postcode is not valid", postcode => PostcodeValidator.isValid(postcode)),
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
        InprogressApplication(address = partialAddress, possibleAddresses = possibleAddresses)
    ) (
      (inprogress) => Some(inprogress.address, inprogress.possibleAddresses)
    ) verifying addressOrManualAddressDefined
  ) 
}
