package uk.gov.gds.ier.transaction.overseas.confirmation

import play.api.data.Forms._
import uk.gov.gds.ier.model.{InprogressOverseas, Stub}
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, ErrorMessages, OverseasForms}

trait ConfirmationForms 
  extends FormKeys
  with ErrorMessages
  with OverseasForms {

  val stubMapping = mapping(
    "foo" -> text
  ) (foo => Stub()) (stub => Some("foo"))

  val confirmationForm = ErrorTransformForm(
    mapping(
      keys.previouslyRegistered.key -> optional(previouslyRegisteredMapping),
      "dateLeftUk" -> optional(stubMapping),
      "firstTimeRegistered" -> optional(stubMapping)
    ) (InprogressOverseas.apply) (InprogressOverseas.unapply)
  )
}
