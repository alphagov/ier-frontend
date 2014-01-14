package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.transaction.ordinary.address.AddressForms
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.constraints.PreviousAddressConstraints
import uk.gov.gds.ier.model.Addresses
import uk.gov.gds.ier.model.PartialAddress
import uk.gov.gds.ier.model.PossibleAddress
import scala.Some
import uk.gov.gds.ier.model.PartialPreviousAddress


trait PreviousAddressForms extends PreviousAddressConstraints {
  self:  FormKeys
    with ErrorMessages
    with AddressForms
    with WithSerialiser =>

  lazy val previousPartialAddressMapping = mapping(
    keys.address.key -> optional(nonEmptyText),
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(nonEmptyText
      .verifying(addressMaxLengthError, _.size <= maxTextFieldLength))
  ) (
    PartialAddress.apply
  ) (
    PartialAddress.unapply
  )

  lazy val previousAddressMapping = mapping(
    keys.movedRecently.key -> optional(boolean),
    keys.findAddress.key -> boolean,
    keys.previousAddress.key -> optional(previousPartialAddressMapping)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )

  val previousAddressForm = ErrorTransformForm(
    mapping(
      keys.previousAddress.key -> optional(previousAddressMapping),
      keys.possibleAddresses.jsonList.key -> optional(nonEmptyText),
      keys.possibleAddresses.postcode.key -> optional(nonEmptyText)
    ) (
      (prevAddress, json, postcode) =>  {
        if (json.isDefined && postcode.isDefined) {
          InprogressOrdinary(
            previousAddress = prevAddress,
            possibleAddresses = Some(PossibleAddress(serialiser.fromJson[Addresses](json.get), postcode.get)))
        }
        else {
          InprogressOrdinary(
            previousAddress = prevAddress,
            possibleAddresses = None)
        }
      }
    ) (
      inprogress =>
        if (inprogress.possibleAddresses.isDefined) {
          Some(inprogress.previousAddress, Some(serialiser.toJson(inprogress.possibleAddresses.get.jsonList)),Some(inprogress.possibleAddresses.get.postcode))
        }
        else {
          Some(inprogress.previousAddress,Option.empty[String],Option.empty[String])
        }
    )
    verifying (previousAddressValidations)
  )
}


