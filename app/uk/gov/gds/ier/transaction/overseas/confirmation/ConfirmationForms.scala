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
      keys.name.key -> stepRequired(nameMapping),
      keys.previousName.key -> stepRequired(previousNameMapping),
      keys.previouslyRegistered.key -> stepRequired(previouslyRegisteredMapping),
      keys.dateLeftUk.key -> stepRequired(dateLeftUkMapping),
      "firstTimeRegistered" -> stepRequired(stubMapping),
      "lastRegisteredToVote" -> stepRequired(lastRegisteredToVoteMapping),
      "registeredAddress" -> stepRequired(stubMapping),
      keys.dob.key -> stepRequired(dobMapping),
      keys.nino.key -> stepRequired(ninoMapping),
      "address" -> stepRequired(stubMapping),
      keys.openRegister.key -> stepRequired(optInMapping),
      "waysToVote" -> stepRequired(stubMapping),
      "postalVote" -> stepRequired(stubMapping),
      keys.contact.key -> stepRequired(contactMapping)
    ) (InprogressOverseas.apply) (InprogressOverseas.unapply)
  )
}
