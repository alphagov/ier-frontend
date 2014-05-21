package uk.gov.gds.ier.transaction.ordinary.nino

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model.{Nino}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.validation.constraints.CommonConstraints

trait NinoForms extends NinoConstraints {
  self:  FormKeys =>

  val ninoForm = ErrorTransformForm(
    mapping(keys.nino.key -> optional(Nino.mapping))
    (
      nino => InprogressOrdinary(nino = nino)
    ) (
      inprogress => Some(inprogress.nino)
    ) verifying (ninoOrNoNinoReasonDefined, ninoIsValidIfProvided)
  )
}

trait NinoConstraints extends CommonConstraints with FormKeys {
  lazy val ninoOrNoNinoReasonDefined = Constraint[InprogressOrdinary](keys.nino.key) {
    application =>
      if (application.nino.isDefined) {
        Valid
      }
      else {
        Invalid("ordinary_nino_error_none_entered", keys.nino.nino)
      }
  }

  lazy val ninoIsValidIfProvided = Constraint[InprogressOrdinary](keys.nino.nino.key) {
    _.nino match {
      case Some(Nino(Some(nino), _)) if NinoValidator.isValid(nino) => Valid
      case Some(Nino(Some(nino), _)) if !NinoValidator.isValid(nino) =>
        Invalid("ordinary_nino_error_incorrect_format", keys.nino.nino)
      case _ => Valid
    }
  }
}

