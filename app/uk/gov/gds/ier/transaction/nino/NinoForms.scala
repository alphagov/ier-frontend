package uk.gov.gds.ier.transaction.nino

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import scala.Some
import uk.gov.gds.ier.validation.constraints.NinoConstraints
import scala.Some
import scala.Some
import uk.gov.gds.ier.model.Nino

trait NinoForms extends NinoConstraints {
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
    mapping(keys.nino.key -> optional(ninoMapping))
    (
      nino => InprogressOrdinary(nino = nino)
    ) (
      inprogress => Some(inprogress.nino)
    ) verifying (ninoOrNoNinoReasonDefined)
  )
}

