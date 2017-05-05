package uk.gov.gds.ier.transaction.ordinary.alreadyRegistered

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait AlreadyRegisteredForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val alreadyRegisteredMapping = single(
    keys.alreadyRegistered.key -> default(boolean, true)
  )

  val alreadyRegisteredForm = ErrorTransformForm(
    mapping(
      keys.alreadyRegistered.key -> default(alreadyRegisteredMapping, true)
    ) (
      alreadyRegistered => InprogressOrdinary(alreadyRegistered = Some(alreadyRegistered))
    ) (
      inprogress => inprogress.alreadyRegistered
    )

  )
}