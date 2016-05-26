package uk.gov.gds.ier.transaction.ordinary.soleOccupancy

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model.{Contact, Nino, PartialAddress, SoleOccupancyOption}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.ValidationError

trait SoleOccupancyForms extends SoleOccupancyConstraints {
  self:  FormKeys =>

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
}

trait SoleOccupancyConstraints extends CommonConstraints with FormKeys {
  lazy val soleOccupancyDefined = Constraint[InprogressOrdinary](keys.soleOccupancy.optIn.key) {
    application =>
      if (application.soleOccupancy.isDefined) {
        Valid
      }
      else {
        Invalid("ordinary_soleOccupancy_error_answerThis", keys.soleOccupancy.optIn)
      }
  }
}

