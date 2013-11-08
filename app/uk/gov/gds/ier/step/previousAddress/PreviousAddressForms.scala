package uk.gov.gds.ier.step.previousAddress

import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.validation.PostcodeValidator
import uk.gov.gds.ier.model.{InprogressApplication, PreviousAddress, Addresses}
import uk.gov.gds.ier.step.address.AddressForms
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._

trait PreviousAddressForms {
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
  ).verifying("Please enter your postcode", 
        p => (p.movedRecently && p.previousAddress.isDefined) || !p.movedRecently)
    .verifying("Please answer this question", 
        p => p.movedRecently || (!p.movedRecently && !p.previousAddress.isDefined))
    
  val previousAddressForm = Form(
    mapping(
      keys.previousAddress.key -> optional(previousAddressMapping)
        .verifying("Please answer this question", previousAddress => previousAddress.isDefined),
      keys.possibleAddresses.key -> optional(possibleAddressMapping)
    ) (
      (prevAddress, possibleAddresses) => 
        InprogressApplication(previousAddress = prevAddress, possibleAddresses = possibleAddresses)
    ) (
      inprogress => Some(inprogress.previousAddress, inprogress.possibleAddresses)
    )
  )
}

