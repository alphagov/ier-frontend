package uk.gov.gds.ier.transaction.ordinary.address

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._
import scala.Some
import uk.gov.gds.ier.validation.constraints.AddressConstraints
import uk.gov.gds.ier.model.Addresses
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
    (json, postcode) => PossibleAddress(serialiser.fromJson[Addresses](json), postcode)
  ) (
    possibleAddress => Some(serialiser.toJson(possibleAddress.jsonList), possibleAddress.postcode)
  )

  lazy val partialAddressMapping = mapping(
    keys.addressLine.key -> optional(nonEmptyText),
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
        InprogressOrdinary(address = partialAddress, possibleAddresses = possibleAddresses)
    ) (
      inprogress => Some(inprogress.address, inprogress.possibleAddresses)
    ) verifying addressOrManualAddressDefined
  ) 

  val addressLookupForm = ErrorTransformForm(
    mapping(
      keys.possibleAddresses.postcode.key -> text
        .verifying("Your postcode is not valid", postcode => PostcodeValidator.isValid(postcode))
    ) (
      postcode => InprogressOrdinary(possibleAddresses = Some(PossibleAddress(jsonList = Addresses(List.empty), postcode = postcode)))
    ) (
      inprogress => inprogress.possibleAddresses.map(_.postcode)
    )
  )
}
