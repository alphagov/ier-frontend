package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation.{
  ErrorTransformForm,
  ErrorMessages,
  FormKeys,
  PostcodeValidator
}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.{
  InprogressOverseas,
  PartialAddress,
  PossibleAddress,
  Addresses
}

trait LastUkAddressForms extends LastUkAddressConstraints {
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
      keys.lastUkAddress.key -> optional(postcodeLookupMapping)
    ) (
      lastUkAddr => InprogressOverseas(
        lastUkAddress = lastUkAddr
      )
    ) (
      inprogress => Some(inprogress.lastUkAddress)
    )
  )

  val lastUkAddressForm = ErrorTransformForm(
    mapping (
      keys.lastUkAddress.key -> optional(partialAddressMapping)
    ) (
      lastUkAddr => InprogressOverseas(
        lastUkAddress = lastUkAddr
      )
    ) (
      inprogress => Some(inprogress.lastUkAddress)
    )
  )

  val selectAddressForm = ErrorTransformForm(
    mapping (
      keys.lastUkAddress.key -> optional(partialAddressMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (
      (lastUkAddr, possibleAddr) => InprogressOverseas(
        lastUkAddress = lastUkAddr,
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        inprogress.lastUkAddress,
        inprogress.possibleAddresses
      )
    )
  )
}


trait LastUkAddressConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val manualAddressMaxLength = Constraint[PartialAddress](keys.lastUkAddress.key) {
    case PartialAddress(_, _, _, Some(manualAddress))
      if manualAddress.size <= maxExplanationFieldLength => Valid
    case PartialAddress(_, _, _, None) => Valid
    case _ => Invalid(addressMaxLengthError, keys.lastUkAddress.manualAddress)
  }

  lazy val postcodeIsValid = Constraint[PartialAddress](keys.lastUkAddress.key) {
    case PartialAddress(_, _, postcode, _)
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.lastUkAddress.postcode)
  }
}
