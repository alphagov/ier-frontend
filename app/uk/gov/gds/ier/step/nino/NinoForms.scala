package uk.gov.gds.ier.step.nino

import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.model.{InprogressApplication, Nino}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.model.InprogressApplication
import scala.Some
import uk.gov.gds.ier.model.Nino

trait NinoForms {
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
    mapping(
      keys.nino.key -> optional(
        ninoMapping.verifying("Please enter your National Insurance number", 
          nino => nino.nino.isDefined || nino.noNinoReason.isDefined)
      ).verifying("Please enter your National Insurance number", nino => nino.isDefined)
    ) (
      nino => InprogressApplication(nino = nino)
    ) (
      inprogress => Some(inprogress.nino)
    )
  )
}

