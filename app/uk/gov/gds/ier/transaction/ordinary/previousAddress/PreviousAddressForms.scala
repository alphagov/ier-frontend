package uk.gov.gds.ier.transaction.ordinary.previousAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
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

  lazy val partialAddressMappingForPreviousAddress = mapping(
    keys.addressLine.key -> optional(nonEmptyText),
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(nonEmptyText)
  ) (
    PartialAddress.apply
  ) (
    PartialAddress.unapply
  ).verifying(
      manualAddressMaxLengthForPreviousAddress,
      postcodeIsValidForPreviousAddress,
      uprnOrManualDefinedForPreviousAddress)

  lazy val partialPreviousAddressMappingForPreviousAddress = mapping(
    keys.movedRecently.key -> optional(boolean),
    keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )

  val manualPartialPreviousAddressMappingForPreviousAddress = mapping(
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
  ).verifying(manualAddressMaxLengthForPreviousAddress, postcodeIsValidForPreviousAddress)

  lazy val postcodeLookupMappingForPreviousAddress = mapping(
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
  ).verifying(postcodeIsValidForlookupForPreviousAddress)

  lazy val possibleAddressesMappingForPreviousAddress = mapping(
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

  val postcodeAddressFormForPreviousAddress = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(postcodeLookupMappingForPreviousAddress)
    ) (
      previousAddress => InprogressOrdinary(
        previousAddress = previousAddress
      )
    ) (
      inprogress => Some(inprogress.previousAddress)
    ).verifying( postcodeIsNotEmptyForPreviousAddress )
  )

  val selectAddressFormForPreviousAddress = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress),
      keys.possibleAddresses.key -> optional(possibleAddressesMappingForPreviousAddress)
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
    ).verifying( selectedAddressIsRequiredForPreviousAddress )
  )

  val manualAddressFormForPreviousAddress = ErrorTransformForm(
    mapping(
      keys.previousAddress.key -> optional(manualPartialPreviousAddressMappingForPreviousAddress)
    ) (
      previousAddress => InprogressOrdinary(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(true),
          previousAddress = previousAddress
      )))
    ) (
      inprogress => inprogress.previousAddress.map(_.previousAddress)
    ).verifying( manualAddressIsRequiredForPreviousAddress )
  )
}


trait PreviousAddressConstraints extends CommonConstraints {
  self: FormKeys
   with ErrorMessages =>

  lazy val manualAddressIsRequiredForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
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

  lazy val selectedAddressIsRequiredForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
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

  lazy val postcodeIsNotEmptyForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(partialAddress) if partialAddress
          .previousAddress.exists(_.postcode == "") => {
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

  lazy val uprnOrManualDefinedForPreviousAddress = Constraint[PartialAddress](keys.previousAddress.key) {
    case partialAddress if partialAddress.uprn.exists(_ != "") => Valid
    case partialAddress if partialAddress.manualAddress.exists(_ != "") => Valid
    case _ => Invalid(
      "Please select your address",
      keys.previousAddress.uprn,
      keys.previousAddress.manualAddress,
      keys.previousAddress
    )
  }

  lazy val manualAddressMaxLengthForPreviousAddress = Constraint[PartialAddress](keys.previousAddress.key) {
    case PartialAddress(_, _, _, Some(manualAddress))
      if manualAddress.size <= maxExplanationFieldLength => Valid
    case PartialAddress(_, _, _, None) => Valid
    case _ => Invalid(addressMaxLengthError, keys.previousAddress.manualAddress)
  }

  lazy val postcodeIsValidForPreviousAddress = Constraint[PartialAddress](keys.previousAddress.key) {
    case PartialAddress(_, _, postcode, _)
      if PostcodeValidator.isValid(postcode) => {
      Valid
    }
    case _ => {
      Invalid("Your postcode is not valid", keys.previousAddress.postcode)
    }
  }

  /**
   * Special version of 'postcodeIsValid' just for Postcode Step.
   * The input type here is different, it is PartialPreviousAddress, wrapping PartialAddress
   * containing the postcode.
   */
  lazy val postcodeIsValidForlookupForPreviousAddress = Constraint[PartialPreviousAddress](keys.previousAddress.key) {
    case PartialPreviousAddress(Some(true), Some(PartialAddress(_, _, postcode, _)))
      if PostcodeValidator.isValid(postcode) => Valid
    case _ => Invalid("Your postcode is not valid", keys.previousAddress.postcode)
  }
}
