package uk.gov.gds.ier.validation.constraints.overseas

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import scala.Some
import uk.gov.gds.ier.validation.constraints.CommonConstraints

trait DateLeftUkConstraints extends CommonConstraints{
  self: ErrorMessages
    with FormKeys =>

  lazy val validateDateLeftUk = Constraint[InprogressOverseas](keys.dateLeftUk.key) {
    application => application.dateLeftUk match {
      case Some(dateLeftUk) => Valid
      case None => Invalid ("Please answer this question", keys.dateLeftUk.month, keys.dateLeftUk.year)
    }
  }
}
