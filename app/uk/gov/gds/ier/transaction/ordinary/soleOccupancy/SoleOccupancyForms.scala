package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import uk.gov.gds.ier.service.ScotlandService

trait SoleOccupancyForms {
  self:  FormKeys
    with ErrorMessages =>

  val scotlandService: ScotlandService

  val soleOccupancyForm = ErrorTransformForm(
    mapping(
      keys.soleOccupancy.optIn.key -> optional(SoleOccupancyOption.mapping),
      keys.address.key -> optional(PartialAddress.mapping)
    )(
      (soleOccupancy, address) => InprogressOrdinary(soleOccupancy = soleOccupancy, address = address)
    )(
      inprogress => Some(inprogress.soleOccupancy, inprogress.address)
    ) verifying (soleOccupancyDefined)
  )

  lazy val soleOccupancyDefined = Constraint[InprogressOrdinary](keys.soleOccupancy.optIn.key) {
    application =>
      // Scotland users don't answer this question
      if (scotlandService.isScot(application)) Valid
      // everyone else must answer this question
      else {
        if (application.soleOccupancy.isDefined) {
          Valid
        }
        else {
          Invalid("ordinary_soleOccupancy_error_answerThis", keys.soleOccupancy.optIn)
        }
      }
  }
}

