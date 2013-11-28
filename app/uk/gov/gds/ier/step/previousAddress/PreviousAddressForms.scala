package uk.gov.gds.ier.step.previousAddress

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.step.address.AddressForms
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.model.InprogressApplication
import scala.Some
import uk.gov.gds.ier.model.PreviousAddress
import uk.gov.gds.ier.validation.constraints.PreviousAddressConstraints

trait PreviousAddressForms extends PreviousAddressConstraints {
  self:  FormKeys
    with ErrorMessages
    with AddressForms
    with WithSerialiser =>

  lazy val previousAddressMapping = mapping(
    keys.movedRecently.key -> boolean,
    keys.previousAddress.key -> optional(addressMapping)
  ) (
    PreviousAddress.apply
  ) (
    PreviousAddress.unapply
  ) verifying(addressExistsIfMovedRecently, movedRecentlyTrueIfAddressProvided)

  val previousAddressForm = ErrorTransformForm(
    mapping(
      keys.previousAddress.key -> optional(previousAddressMapping)
        .verifying("Please answer this question", previousAddress => previousAddress.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) (
      (prevAddress, possibleAddresses) => 
        InprogressApplication(
          previousAddress = prevAddress, 
          possibleAddresses = possibleAddresses)
    ) (
      inprogress => Some(inprogress.previousAddress, inprogress.possibleAddresses)
    )
  )
}


