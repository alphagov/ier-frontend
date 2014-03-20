package uk.gov.gds.ier.transaction.crown.previousAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.InprogressCrown
import scala.Some
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.PossibleAddress

trait PreviousAddressForms extends PreviousAddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  // address mapping for select address page - the address part
  lazy val partialAddressMappingForPreviousAddress = mapping(
    keys.addressLine.key -> optional(nonEmptyText),
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(manualPartialAddressLinesMappingForPreviousAddress)
  ) (
    PartialAddress.apply
  ) (
    PartialAddress.unapply
  ).verifying(
      postcodeIsValidForPreviousAddress,
      uprnOrManualDefinedForPreviousAddress)

  // address mapping for manual address - the address individual lines part
  lazy val manualPartialAddressLinesMappingForPreviousAddress = mapping(
    keys.lineOne.key -> optional(nonEmptyText),
    keys.lineTwo.key -> optional(text),
    keys.lineThree.key -> optional(text),
    keys.city.key -> optional(nonEmptyText)
  ) (
    PartialManualAddress.apply
  ) (
    PartialManualAddress.unapply
  ).verifying(lineOneIsRequredForPreviousAddress, cityIsRequiredForPreviousAddress)

  lazy val partialPreviousAddressMappingForPreviousAddress = mapping(
    keys.movedRecently.key -> optional(boolean),
    keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )

  // address mapping for manual address - the address parent wrapper part
  val manualPartialPreviousAddressMappingForPreviousAddress = mapping(
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(manualPartialAddressLinesMappingForPreviousAddress)
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
  ).verifying(postcodeIsValidForPreviousAddress)


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
      previousAddress => InprogressCrown(
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
      (previousAddress, possibleAddr) => InprogressCrown(
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
      previousAddress => InprogressCrown(
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

  lazy val manualAddressIsRequiredForPreviousAddress = Constraint[InprogressCrown](keys.previousAddress.key) {
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

  lazy val selectedAddressIsRequiredForPreviousAddress = Constraint[InprogressCrown](keys.previousAddress.key) {
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

  lazy val postcodeIsNotEmptyForPreviousAddress = Constraint[InprogressCrown](keys.previousAddress.key) {
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

  lazy val lineOneIsRequredForPreviousAddress = Constraint[PartialManualAddress](
    keys.previousAddress.manualAddress.key) {
    case PartialManualAddress(Some(_), _, _, _) => Valid
    case _ => Invalid(lineOneIsRequiredError, keys.previousAddress.manualAddress.lineOne)
  }

  lazy val cityIsRequiredForPreviousAddress = Constraint[PartialManualAddress](
    keys.previousAddress.manualAddress.key) {
    case PartialManualAddress(_, _, _, Some(_)) => Valid
    case _ => Invalid(cityIsRequiredError, keys.previousAddress.manualAddress.city)
  }
}
