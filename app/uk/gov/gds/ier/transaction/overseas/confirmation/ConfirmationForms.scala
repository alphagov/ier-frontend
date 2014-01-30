package uk.gov.gds.ier.transaction.overseas.confirmation

import play.api.data.Forms._
import uk.gov.gds.ier.model.{InprogressOverseas, Stub}
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages, OverseasForms}
import uk.gov.gds.ier.validation.constraints.CommonConstraints

trait ConfirmationForms 
  extends FormKeys
  with ErrorMessages
  with OverseasForms
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
      "registeredAddress" -> stepRequired(stubMapping),
      "dateOfBirth" -> stepRequired(dobAndReasonMapping)
    ) (InprogressOverseas.apply) (InprogressOverseas.unapply)
  )
}