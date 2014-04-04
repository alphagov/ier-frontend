package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import play.api.data.Forms._
import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PreviouslyRegistered}
import uk.gov.gds.ier.validation.constraints.PreviouslyRegisteredConstraints
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait PreviouslyRegisteredForms extends PreviouslyRegisteredConstraints {
  self: FormKeys
  with ErrorMessages =>

  val previouslyRegisteredMapping = mapping(
    keys.hasPreviouslyRegistered.key -> boolean
  ) (PreviouslyRegistered.apply) (PreviouslyRegistered.unapply)

  val previouslyRegisteredForm = ErrorTransformForm(
    mapping (
      keys.previouslyRegistered.key -> optional(previouslyRegisteredMapping)
    ) (
      prevRegistered => InprogressOverseas(previouslyRegistered = prevRegistered)
    ) (
      inprogress => Some(inprogress.previouslyRegistered)
    ) verifying previouslyRegisteredFilled
  )
}
