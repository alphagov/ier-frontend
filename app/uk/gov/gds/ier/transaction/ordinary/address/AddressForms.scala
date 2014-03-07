package uk.gov.gds.ier.transaction.ordinary.address

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation.{
  ErrorTransformForm,
  ErrorMessages,
  FormKeys,
  PostcodeValidator}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{
  InprogressOrdinary,
  PartialAddress,
  PossibleAddress,
  Addresses}

trait AddressForms extends AddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  lazy val partialAddressMapping = mapping(
    keys.addressLine.key -> optional(nonEmptyText),
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(nonEmptyText)
  ) (
    PartialAddress.apply
  ) (
    PartialAddress.unapply
  ).verifying(manualAddressMaxLength, postcodeIsValid, uprnOrManualDefined)

  lazy val manualPartialAddressMapping = mapping(
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(nonEmptyText)
  ) (
    (postcode, manualAddress) => PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = postcode,
      manualAddress = manualAddress
    )
  ) (
    partial => Some(
      partial.postcode,
      partial.manualAddress
    )
  ).verifying(manualAddressMaxLength, postcodeIsValid)

  lazy val postcodeLookupMapping = mapping(
    keys.postcode.key -> nonEmptyText
  ) (
    postcode => PartialAddress(
      addressLine = None,
      uprn = None,
      postcode = postcode,
      manualAddress = None
    )
  ) (
    partial => Some(partial.postcode)
  ).verifying(postcodeIsValid)

  lazy val possibleAddressesMapping = mapping(
    keys.jsonList.key -> nonEmptyText,
    keys.postcode.key -> nonEmptyText
  ) (
    (json, postcode) => PossibleAddress(
      serialiser.fromJson[Addresses](json),
      postcode
    )
  ) (
    possibleAddress => Some(
      serialiser.toJson(possibleAddress.jsonList),
      possibleAddress.postcode
    )
  )

  val lookupAddressForm = ErrorTransformForm(
    mapping (
      keys.address.key -> optional(postcodeLookupMapping)
    ) (
      addr => InprogressOrdinary(
        address = addr
      )
    ) (
      inprogress => Some(inprogress.address)
    ).verifying( postcodeIsNotEmpty )
  )

  val addressForm = ErrorTransformForm(
    mapping (
      keys.address.key -> optional(partialAddressMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (
      (addr, possibleAddr) => InprogressOrdinary(
        address = addr,
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        inprogress.address,
        inprogress.possibleAddresses
      )
    ).verifying( addressIsRequired )
  )

  val manualAddressForm = ErrorTransformForm(
    mapping(
      keys.address.key -> optional(manualPartialAddressMapping)
    ) (
      addr => InprogressOrdinary(address = addr)
    ) (
      inprogress => Some(inprogress.address)
    ).verifying( manualAddressIsRequired )
  )
}


trait AddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val manualAddressIsRequired = Constraint[InprogressOrdinary](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(partialAddress) if partialAddress.manualAddress.isDefined => Valid
        case _ => Invalid("Please answer this question", keys.address.manualAddress)
      }
  }

  lazy val postcodeIsNotEmpty = Constraint[InprogressOrdinary](keys.address.key) {
    inprogress =>
      inprogress.address match {
        case Some(partialAddress) if partialAddress.postcode == "" => {
          Invalid("Please enter your postcode", keys.address.postcode)
        }
        case None => Invalid("Please enter your postcode", keys.address.postcode)
        case _ => Valid
      }
  }

  lazy val addressIsRequired = Constraint[InprogressOrdinary](keys.address.key) {
    inprogress =>
      if (inprogress.address.isDefined) Valid
      else Invalid("Please answer this question", keys.address)
  }

  lazy val uprnOrManualDefined = Constraint[PartialAddress](keys.address.key) {
    case partialAddress if partialAddress.uprn.exists(_ != "") => Valid
    case partialAddress if partialAddress.manualAddress.exists(_ != "") => Valid
    case _ => Invalid(
      "Please select your address",
      keys.address.uprn,
      keys.address.manualAddress,
      keys.address
    )
  }

  lazy val manualAddressMaxLength = Constraint[PartialAddress](keys.address.key) {
    case PartialAddress(_, _, _, Some(manualAddress))
      if manualAddress.size <= maxExplanationFieldLength => Valid
    case PartialAddress(_, _, _, None) => Valid
    case _ => Invalid(addressMaxLengthError, keys.address.manualAddress)
  }

  lazy val postcodeIsValid = Constraint[PartialAddress](keys.address.key) {
    case PartialAddress(_, _, postcode, _)
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.address.postcode)
  }
}
