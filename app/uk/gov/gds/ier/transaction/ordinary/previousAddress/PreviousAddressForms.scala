package uk.gov.gds.ier.transaction.ordinary.previousAddress

import play.api.data.Forms._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.model.PartialManualAddress
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressForms extends PreviousAddressConstraints {
  self: FormKeys
  with ErrorMessages
  with WithSerialiser =>

  private[previousAddress] lazy val possibleAddressesMappingForPreviousAddress = mapping(
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

  private[previousAddress] val previousAddressForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(PartialPreviousAddress.mapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMappingForPreviousAddress)
    ) (
      (previousAddress, possibleAddresses) => InprogressOrdinary(
        previousAddress = previousAddress,
        possibleAddresses = possibleAddresses
      )
    ) (
      inprogress => Some(
        inprogress.previousAddress,
        inprogress.possibleAddresses
      )
    ) verifying (
      postcodeIsValidForPreviousAddress,
      manualAddressLineOneRequired,
      cityIsRequiredForPreviousAddress
    )
  )

  /** root validator - postcode page */
  private[previousAddress] val postcodeStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      postcodeIsNotEmptyForPreviousAddress
    )
  )

  /** root validator - select page */
  private[previousAddress] val selectStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      selectedAddressIsRequiredForPreviousAddress
    )
  )

  /** root validator - manual address */
  private[previousAddress] val manualStepForm = ErrorTransformForm(
    previousAddressForm.mapping.verifying(
      manualAddressIsRequiredForPreviousAddress
    )
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
          Invalid("Please answer this question", keys.previousAddress.previousAddress.manualAddress)
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
          Invalid("Please answer this question", keys.previousAddress.previousAddress.uprn)
        }
      }
  }

  lazy val postcodeIsNotEmptyForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      inprogress.previousAddress match {
        case Some(PartialPreviousAddress(_,Some(partialAddress)))
          if (partialAddress.postcode == "") => {
            Invalid("Please enter your postcode", keys.previousAddress.previousAddress.postcode)
        }
        case Some(PartialPreviousAddress(_,None)) => {
          Invalid("Please enter your postcode", keys.previousAddress.previousAddress.postcode)
        }
        case None => {
          Invalid("Please enter your postcode", keys.previousAddress.previousAddress.postcode)
        }
        case _ => {
          Valid
        }
      }
  }

  lazy val postcodeIsValidForPreviousAddress = Constraint[InprogressOrdinary](keys.previousAddress.key) {
    inprogress =>
      val possiblePostcode = inprogress.previousAddress.flatMap(_.previousAddress).map(_.postcode)
      possiblePostcode match {
        case Some(postcode) if !PostcodeValidator.isValid(postcode) => Invalid(
          "Your postcode is not valid",
          keys.previousAddress.previousAddress.postcode
        )
        case _ => Valid
      }
  }

  lazy val manualAddressLineOneRequired = Constraint[InprogressOrdinary](
      keys.previousAddress.manualAddress.key) { inprogress =>
    val manualAddress = inprogress.previousAddress.flatMap(_.previousAddress).flatMap(_.manualAddress)

    manualAddress match {
      case Some(PartialManualAddress(None, _, _, _)) => Invalid(
        lineOneIsRequiredError,
        keys.previousAddress.previousAddress.manualAddress.lineOne
      )
      case _ => Valid
    }
  }

  lazy val cityIsRequiredForPreviousAddress = Constraint[InprogressOrdinary](
      keys.previousAddress.manualAddress.key) { inprogress =>
    val manualAddress = inprogress.previousAddress.flatMap(_.previousAddress).flatMap(_.manualAddress)

    manualAddress match {
      case Some(PartialManualAddress(_, _, _, None)) => Invalid(
        cityIsRequiredError,
        keys.previousAddress.previousAddress.manualAddress.city
      )
      case _ => Valid
    }
  }
}
