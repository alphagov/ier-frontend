package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model.{SoleOccupancyOption, Nino, Contact}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.ValidationError

trait SoleOccupancyForms extends SoleOccupancyConstraints {
  self:  FormKeys =>

  val soleOccupancyForm = ErrorTransformForm(
    mapping(
      keys.soleOccupancy.key -> optional(SoleOccupancyOption.mapping)
    )(
      (soleOccupancy) => InprogressOrdinary(soleOccupancy = soleOccupancy)
    )(
      inprogress => Some(inprogress.soleOccupancy)
    ) verifying (soleOccupancyDefined)
  )
}

trait SoleOccupancyConstraints extends CommonConstraints with FormKeys {
  lazy val soleOccupancyDefined = Constraint[InprogressOrdinary](keys.soleOccupancy.key) {
    application =>
      if (application.soleOccupancy.isDefined) {
        Valid
      }
      else {
        Invalid("ordinary_soleOccupancy_error_answerThis", keys.soleOccupancy.optIn)
      }
  }
}

