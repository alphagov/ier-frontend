package uk.gov.gds.ier.transaction.overseas.confirmation

import play.api.data.Forms._
import uk.gov.gds.ier.model.{InprogressOverseas, Stub}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages}
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.LastUkAddressForms
import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredForms
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkForms
import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthForms
import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteForms
import uk.gov.gds.ier.transaction.overseas.nino.NinoForms

trait ConfirmationForms
  extends FormKeys
  with ErrorMessages
  with WithSerialiser
  with PreviouslyRegisteredForms
  with DateLeftUkForms
  with DateOfBirthForms
  with LastRegisteredToVoteForms
  with NinoForms
  with LastUkAddressForms
  with CommonConstraints {

  val stubMapping = mapping(
    "foo" -> text
  ) (foo => Stub()) (stub => Some("foo"))

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.previouslyRegistered.key -> stepRequired(previouslyRegisteredMapping),
      keys.dateLeftUk.key -> stepRequired(dateLeftUkMapping),
      "firstTimeRegistered" -> stepRequired(stubMapping),
      "lastRegisteredToVote" -> stepRequired(lastRegisteredToVoteMapping),
      "dateOfBirth" -> stepRequired(dobAndReasonMapping),
      "name" -> stepRequired(stubMapping),
      keys.nino.key -> stepRequired(ninoMapping),
      keys.lastUkAddress.key -> stepRequired(partialAddressMapping),
      "address" -> stepRequired(stubMapping),
      keys.possibleAddresses.key -> optional(possibleAddressesMapping)
    ) (InprogressOverseas.apply) (InprogressOverseas.unapply)
  )
}
