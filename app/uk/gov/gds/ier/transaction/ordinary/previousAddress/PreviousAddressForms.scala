package uk.gov.gds.ier.transaction.previousAddress

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.transaction.address.AddressForms
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Forms._
import uk.gov.gds.ier.model.{InprogressOrdinary, PartialPreviousAddress}
import uk.gov.gds.ier.validation.constraints.PreviousAddressConstraints

trait PreviousAddressForms extends PreviousAddressConstraints {
  self:  FormKeys
    with ErrorMessages
    with AddressForms
    with WithSerialiser =>

  lazy val previousAddressMapping = mapping(
    keys.movedRecently.key -> boolean,
    keys.previousAddress.key -> optional(partialAddressMapping)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  ) verifying(addressExistsIfMovedRecently, movedRecentlyTrueIfAddressProvided)

  val previousAddressForm = ErrorTransformForm(
    mapping(
      keys.previousAddress.key -> optional(previousAddressMapping)
        .verifying("Please answer this question", previousAddress => previousAddress.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) (
      (prevAddress, possibleAddresses) =>
        InprogressOrdinary(
          previousAddress = prevAddress, 
          possibleAddresses = possibleAddresses)
    ) (
      inprogress => Some(inprogress.previousAddress, inprogress.possibleAddresses)
    )
  )
}


