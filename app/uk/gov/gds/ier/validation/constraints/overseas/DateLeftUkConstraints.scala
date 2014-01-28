package uk.gov.gds.ier.validation.constraints.overseas

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model._
import scala.Some
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import org.joda.time.{DateTime, Months}

trait DateLeftUkConstraints extends CommonConstraints{
  self: ErrorMessages
    with FormKeys =>

  lazy val validateDateLeftUk = Constraint[InprogressOverseas](keys.dateLeftUk.key) {
    application => application.dateLeftUk match {
      case Some(dateLeftUk) => validateDateLeftUkNotOver15Years(dateLeftUk)
      case None => Invalid(
        "Please answer this question",
        keys.dateLeftUk.month,
        keys.dateLeftUk.year
      )
    }
  }

  def validateDateLeftUkNotOver15Years(dateLeftUk:DateLeftUk) = {
    val leftUk = new DateTime().withMonthOfYear(dateLeftUk.month).withYear(dateLeftUk.year)
    val monthDiff = Months.monthsBetween(leftUk, DateTime.now()).getMonths()
    if (monthDiff >= 15 * 12) {
      Invalid(
        "You cannot register, because it has been over 15 years since you left the UK",
        keys.dateLeftUk.month,
        keys.dateLeftUk.year
      )
    }
    else Valid
  }
}
