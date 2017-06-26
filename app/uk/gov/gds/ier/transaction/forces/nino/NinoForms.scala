package uk.gov.gds.ier.transaction.forces.nino

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NinoConstraints
import uk.gov.gds.ier.model.{Nino, Contact}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait NinoForms extends NinoForcesConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val ninoMapping = mapping(
    keys.nino.key -> optional(nonEmptyText),
    keys.noNinoReason.key -> optional(nonEmptyText
      .verifying(noNinoReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (
    Nino.apply
  ) (
    Nino.unapply
  ).verifying(ninoIsValidIfProvided)

  val ninoForm = ErrorTransformForm(
    mapping(
      keys.nino.key -> optional(ninoMapping),
      keys.contact.key -> optional(Contact.mapping))
    (
        (nino, contact) => InprogressForces(nino = nino, contact = contact)
    ) (
      inprogress => Some(inprogress.nino, inprogress.contact)
    ) verifying (ninoOrNoNinoReasonDefinedForces)
  )
}

trait NinoForcesConstraints extends NinoConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val ninoOrNoNinoReasonDefinedForces = Constraint[InprogressForces](keys.nino.key) {
    application =>
      if (application.nino.isDefined) {
        Valid
      }
      else {
        Invalid("Please enter your National Insurance number", keys.nino.nino)
      }
  }
}
