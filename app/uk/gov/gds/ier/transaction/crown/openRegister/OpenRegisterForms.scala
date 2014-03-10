package uk.gov.gds.ier.transaction.crown.openRegister

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.InprogressCrown
import play.api.data.Forms._

trait OpenRegisterForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val openRegisterOptInMapping = single(
    keys.optIn.key -> boolean
  )

  val openRegisterForm = ErrorTransformForm(
    mapping(
      keys.openRegister.key -> optional(openRegisterOptInMapping)
    ) (
      openRegister => InprogressCrown(
        openRegisterOptin = openRegister.orElse(Some(true))
      )
    ) (
      inprogress => Some(inprogress.openRegisterOptin)
    )
  )
}

