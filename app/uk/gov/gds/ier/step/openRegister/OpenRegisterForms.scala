package uk.gov.gds.ier.step.openRegister

import uk.gov.gds.ier.validation.{TransformedForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.InprogressApplication
import play.api.data.Form
import play.api.data.Forms._

trait OpenRegisterForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val openRegisterOptInMapping = single(
    keys.optIn.key -> boolean
  )

  val openRegisterForm = TransformedForm(
    mapping(
      keys.openRegister.key -> optional(openRegisterOptInMapping)
    ) (
      openRegister => InprogressApplication(
        openRegisterOptin = openRegister.orElse(Some(true))
      )
    ) (
      inprogress => Some(inprogress.openRegisterOptin)
    )
  )
}

