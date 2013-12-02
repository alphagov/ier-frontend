package uk.gov.gds.ier.step.nino

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model.InprogressApplication
import scala.Some
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.validation.constraints.NinoConstraints
import uk.gov.gds.ier.model.InprogressApplication
import scala.Some
import uk.gov.gds.ier.model.Nino

trait NinoForms extends NinoConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val ninoMapping = mapping(
    keys.nino.key -> optional(nonEmptyText
      .verifying("Your National Insurance number is not correct", nino => NinoValidator.isValid(nino))),
    keys.noNinoReason.key -> optional(nonEmptyText
      .verifying(noNinoReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (
    Nino.apply
  ) (
    Nino.unapply
  )

  val ninoForm = ErrorTransformForm(
    mapping(keys.nino.key -> optional(ninoMapping))
    (
      nino => InprogressApplication(nino = nino)
    ) (
      inprogress => Some(inprogress.nino)
    ) verifying (ninoOrNoNinoReasonDefined)
  )
}

