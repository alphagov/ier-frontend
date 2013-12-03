package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.DOB
import org.joda.time.{DateTime, DateMidnight}

object DateValidator {

  lazy val minimumAge = 16
  lazy val maximumAge = 115

  def isExistingDateInThePast(dateOfBirth: DOB) = {
    try {
      parseToDateMidnight(dateOfBirth).isBeforeNow
    } catch {
      case ex: Exception => false
    }
  }

  def isTooOldToBeAlive(dateOfBirth: DOB) = {
    parseToDateMidnight(dateOfBirth).plusYears(maximumAge).isBefore(DateTime.now.toDateMidnight.plusDays(1))
  }

  def isTooYoungToRegister(dateOfBirth: DOB) = {
    parseToDateMidnight(dateOfBirth).plusYears(minimumAge).isAfter(DateTime.now.toDateMidnight)
  }

  private def parseToDateMidnight(dateOfBirth: DOB) = {
    new DateMidnight(
      dateOfBirth.year, 
      dateOfBirth.month, 
      dateOfBirth.day)
  }
}
