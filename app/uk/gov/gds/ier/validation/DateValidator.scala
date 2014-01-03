package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.DOB
import org.joda.time.{DateTime, DateMidnight}

object DateValidator {

  lazy val minimumAge = 16
  lazy val maximumAge = 115


  def isExistingDate(dateOfBirth: DOB):Option[DateMidnight] = {
    try {
      Some(parseToDateMidnight(dateOfBirth))
    } catch {
      case ex: Exception => None
    }
  }

  def isExistingDateInThePast(dateOfBirth: DateMidnight) = {
    try {
      dateOfBirth.isBeforeNow
    } catch {
      case ex: Exception => false
    }
  }

  def isTooOldToBeAlive(dateOfBirth: DateMidnight) = {
    try {
      dateOfBirth.plusYears(maximumAge).isBefore(DateTime.now.toDateMidnight.plusDays(1))
    } catch {
      case ex: Exception => false
    }
  }

  def isTooYoungToRegister(dateOfBirth: DOB) = {
    try {
      parseToDateMidnight(dateOfBirth).plusYears(minimumAge).isAfter(DateTime.now.toDateMidnight)
    } catch {
      case ex: Exception => false
    }
  }

  private def parseToDateMidnight(dateOfBirth: DOB) = {
    new DateMidnight(
      dateOfBirth.year, 
      dateOfBirth.month, 
      dateOfBirth.day)
  }
}
