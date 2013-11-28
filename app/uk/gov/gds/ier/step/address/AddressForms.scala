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

trait AddressForms {
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

  lazy val addressMapping = mapping(
    keys.address.key -> optional(nonEmptyText
      .verifying(addressMaxLengthError, _.size <= maxTextFieldLength)
    ).verifying("Please select your address", 
      address => address.exists(_ != "Select your address")),
    keys.postcode.key -> nonEmptyText
      .verifying("Your postcode is not valid", 
        postcode => PostcodeValidator.isValid(postcode))
  ) (
    Address.apply
  ) (
    Address.unapply
  )  
    
  val addressForm = TransformedForm(
    mapping(
      keys.address.key -> optional(addressMapping)
        .verifying("Please answer this question", _.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) (
      (address, possibleAddresses) => 
        InprogressApplication(address = address, possibleAddresses = possibleAddresses)
    ) (
      inprogress => Some(inprogress.address, inprogress.possibleAddresses)
    )
  )
}
