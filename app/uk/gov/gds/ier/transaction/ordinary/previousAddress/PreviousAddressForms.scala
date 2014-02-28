package uk.gov.gds.ier.transaction.ordinary.previousAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.PossibleAddress
import scala.Some
import uk.gov.gds.ier.transaction.ordinary.address.AddressForms
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.InprogressOrdinary
import scala.Some
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.model.PossibleAddress

trait PreviousAddressForms extends PreviousAddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  lazy val partialPreviousAddressMapping = mapping(
    keys.movedRecently.key -> optional(boolean),
    keys.previousAddress.key -> optional(partialAddressMappingNew)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )

  //TODO: remove when AddressForm is updated with new definition, then reuse here too
  lazy val partialAddressMappingNew = mapping(
    keys.addressLine.key -> optional(nonEmptyText),
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(nonEmptyText)
  ) (
    PartialAddress.apply
  ) (
    PartialAddress.unapply
  ).verifying(manualAddressMaxLength, postcodeIsValid, uprnOrManualDefined)

  lazy val manualPartialPreviousAddressMapping = mapping(
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
    postcode => PartialPreviousAddress(
      movedRecently = Some(true),
      previousAddress = Some(PartialAddress(
        addressLine = None,
        uprn = None,
        postcode = postcode,
        manualAddress = None)
      )
    )
  ) (
    partialPreviousAddress => partialPreviousAddress.previousAddress.map(_.postcode)
  ).verifying(postcodeIsValidForPPA)

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

  val postcodeAddressForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(postcodeLookupMapping)
    ) (
      previousAddress => InprogressOrdinary(
        previousAddress = previousAddress
      )
    ) (
      inprogress => Some(inprogress.previousAddress)
    ).verifying( postcodeIsNotEmpty )
  )

  val selectAddressForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(partialAddressMappingNew),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (
      (previousAddress, possibleAddr) => InprogressOrdinary(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(true),
          previousAddress = previousAddress
        )),
        possibleAddresses = possibleAddr
      )
    ) (
      inprogress => Some(
        inprogress.previousAddress.flatMap(_.previousAddress),
        inprogress.possibleAddresses)
    ).verifying( selectedAddressIsRequired )
  )

  val manualAddressForm = ErrorTransformForm(
    mapping(
      keys.previousAddress.key -> optional(manualPartialPreviousAddressMapping)
    ) (
      previousAddress => InprogressOrdinary(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(true),
          previousAddress = previousAddress
      )))
    ) (
      inprogress => inprogress.previousAddress.map(_.previousAddress)
    ).verifying( manualAddressIsRequired )
  )
}


trait PreviousAddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val manualAddressIsRequired = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress) if partialAddress.previousAddress
          .flatMap(_.manualAddress).isDefined && partialAddress
          .previousAddress.exists(_.postcode != "") => {
          Valid
        }
        case _ => {
          Invalid("Please answer this question", keys.previousAddress.manualAddress)
        }
      }
  }

  lazy val selectedAddressIsRequired = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress)
          if partialAddress.previousAddress.exists(_.postcode != "")
          && (partialAddress.previousAddress.flatMap(_.uprn).isDefined
          ||  partialAddress.previousAddress.flatMap(_.manualAddress).isDefined) => {
          Valid
        }
        case _ => {
          Invalid("Please answer this question", keys.previousAddress)
        }
      }
  }

  lazy val postcodeIsNotEmpty = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress) if partialAddress
          .previousAddress.exists(_.postcode == "") => {  // FIXME: not sure with the options handling!
          Invalid("Please enter your postcode", keys.previousAddress.postcode)
        }
        case None => {
          Invalid("Please enter your postcode", keys.previousAddress.postcode)
        }
        case _ => {
          Valid
        }
      }
  }

  lazy val uprnOrManualDefined = Constraint[PartialAddress](keys.previousAddress.key) {
    case partialAddress if partialAddress.uprn.exists(_ != "") => Valid
    case partialAddress if partialAddress.manualAddress.exists(_ != "") => Valid
    case _ => Invalid(
      "Please select your address",
      keys.previousAddress.uprn,
      keys.previousAddress.manualAddress,
      keys.previousAddress
    )
  }

  lazy val manualAddressMaxLength = Constraint[PartialAddress](keys.previousAddress.key) {
    case PartialAddress(_, _, _, Some(manualAddress))
      if manualAddress.size <= maxExplanationFieldLength => Valid
    case PartialAddress(_, _, _, None) => Valid
    case _ => Invalid(addressMaxLengthError, keys.previousAddress.manualAddress)
  }

  lazy val postcodeIsValid = Constraint[PartialAddress](keys.previousAddress.key) {
    case PartialAddress(_, _, postcode, _)
      if PostcodeValidator.isValid(postcode) => {
      Valid
    }
    case _ => {
      Invalid("Your postcode is not valid", keys.previousAddress.postcode)
    }
  }

  // FIXME: re-use postcodeIsValid
  lazy val postcodeIsValidForPPA = Constraint[PartialPreviousAddress](keys.previousAddress.key) {
    case PartialPreviousAddress(Some(true), Some(PartialAddress(_, _, postcode, _)))
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.previousAddress.postcode)
  }
}
