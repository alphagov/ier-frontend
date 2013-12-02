package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import play.api.data.validation.{Valid, Invalid, Constraint}
import uk.gov.gds.ier.model.InprogressApplication

trait NinoConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val ninoOrNoNinoReasonDefined = Constraint[InprogressApplication](keys.nino.key) {
    application =>
      if (application.nino.isDefined) {
        Valid
      }
      else {
        Invalid("Please enter your National Insurance number", keys.nino.nino)
      }
  }
}
