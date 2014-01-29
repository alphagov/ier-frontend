package uk.gov.gds.ier.transaction.overseas.confirmation

import play.api.data.Forms._
import uk.gov.gds.ier.model.{InprogressOverseas, Stub}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredForms
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressForms

trait ConfirmationForms 
  extends FormKeys
  with ErrorMessages
  with WithSerialiser
  with PreviouslyRegisteredForms
  with LastUkAddressForms
  with CommonConstraints {

  val stubMapping = mapping(
    "foo" -> text
  ) (foo => Stub()) (stub => Some("foo"))

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.previouslyRegistered.key -> stepRequired(previouslyRegisteredMapping),
      "dateLeftUk" -> stepRequired(stubMapping),
      "firstTimeRegistered" -> stepRequired(stubMapping),
      "name" -> stepRequired(stubMapping),
      keys.lastUkAddress.key -> stepRequired(partialAddressMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (InprogressOverseas.apply) (InprogressOverseas.unapply)
  )
}
