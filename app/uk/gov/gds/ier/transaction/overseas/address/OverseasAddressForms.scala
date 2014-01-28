package uk.gov.gds.ier.transaction.overseas.address

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressOverseas, OverseasAddress}
import uk.gov.gds.ier.validation.constraints.PreviouslyRegisteredConstraints
import uk.gov.gds.ier.validation.constraints.AddressConstraints

trait OverseasAddressForms extends AddressConstraints {
  self: FormKeys
  with ErrorMessages =>

  val overseasAddressMapping = mapping(
    keys.country.key -> text,
    keys.address.key -> text
  ) (OverseasAddress.apply) (OverseasAddress.unapply)

  val addressForm = ErrorTransformForm(
    mapping (
      keys.previouslyRegistered.key -> optional(previouslyRegisteredMapping)
    ) (
      prevRegistered => InprogressOverseas(previouslyRegistered = prevRegistered)
    ) (
      inprogress => Some(inprogress.previouslyRegistered)
    ) verifying previouslyRegisteredFilled
  )
}
