package uk.gov.gds.ier.transaction.ordinary.previousAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.InprogressOrdinary
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.model.PartialManualAddress

trait PreviousAddressForms extends PreviousAddressConstraints with CommonForms {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>


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
  ).verifying (postcodeIsValidForPreviousAddress)

  lazy val partialPreviousAddressMappingForConfirmationForm = mapping(
    keys.movedRecently.key -> optional(movedHouseMapping),
    keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )

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
      keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress)
    ) (
      previousAddress => InprogressOrdinary(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(MovedHouseOption.Yes),
          previousAddress = previousAddress
        ))
      )
    ) (
      inprogress => Some(inprogress.previousAddress.map(pa => pa.previousAddress).getOrElse(None))
    ).verifying( postcodeIsNotEmptyForPreviousAddress )
  )

  val selectAddressFormForPreviousAddress = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress),
      keys.possibleAddresses.key -> optional(possibleAddressesMappingForPreviousAddress)
    ) (
      (previousAddress, possibleAddr) => InprogressOrdinary(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(MovedHouseOption.Yes),
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
      keys.previousAddress.key -> optional(partialAddressMappingForPreviousAddress)
    ) (
      previousAddress => InprogressOrdinary(
        previousAddress = Some(PartialPreviousAddress(
          movedRecently = Some(MovedHouseOption.Yes),
          previousAddress = previousAddress
        ))
      )
    ) (
      inprogress => Some(inprogress.previousAddress.map(pa => pa.previousAddress).getOrElse(None))
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
          Invalid("Please answer this question", keys.previousAddress.uprn)
        }
      }
  }

  lazy val postcodeIsNotEmptyForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(PartialPreviousAddress(_,Some(partialAddress)))
          if (partialAddress.postcode == "") => {
            Invalid("Please enter your postcode", keys.previousAddress.postcode)
        }
        case Some(PartialPreviousAddress(_,None)) => {
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

  lazy val postcodeIsValidForPreviousAddress = Constraint[PartialAddress](keys.previousAddress.key) {
    case PartialAddress(_, _, postcode, _)
      if PostcodeValidator.isValid(postcode) => {
      Valid
    }
    case _ => {
      Invalid("Your postcode is not valid", keys.previousAddress.postcode)
    }
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
