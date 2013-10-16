package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.DateOfBirth
import org.joda.time.{DateTime, DateMidnight}

object DateValidator {

  val minimumAge = 16
  val maximumAge = 115

  def isExistingDateInThePast(dateOfBirth: DateOfBirth) = {
    try {
      new DateMidnight(dateOfBirth.year, dateOfBirth.month, dateOfBirth.day).isBeforeNow
    } catch {
      case ex: Exception => false
    }
  }

  def isTooOldToBeAlive(dateOfBirth: DateOfBirth) = {
    midnight(dateOfBirth).plusYears(maximumAge).isBefore(DateTime.now.toDateMidnight.plusDays(1))
  }

  def isTooYoungToRegister(dateOfBirth: DateOfBirth) = {
    midnight(dateOfBirth).plusYears(minimumAge).isAfter(DateTime.now.toDateMidnight)
  }

  private def midnight(dateOfBirth: DateOfBirth) = new DateMidnight(dateOfBirth.year, dateOfBirth.month, dateOfBirth.day)
}
