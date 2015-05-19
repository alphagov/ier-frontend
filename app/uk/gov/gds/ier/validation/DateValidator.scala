package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.DOB
import org.joda.time.{DateTime, DateMidnight}
import org.joda.time.Months
import uk.gov.gds.ier.model.DateLeft

object DateValidator {

  lazy val minimumScotAge = 14
  lazy val minimumAge = 16
  lazy val maximumAge = 115
  lazy val maximumCitizenshipDuration = 115


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
    isDateBefore(dateOfBirth, maximumAge)
  }

  def isCitizenshipTooOld(dateOfCitizenship: DateMidnight) =  {
    isDateBefore(dateOfCitizenship, maximumCitizenshipDuration)
  }


  def isTooYoungToRegister(dateOfBirth: DOB) = {
    try {
      parseToDateMidnight(dateOfBirth).plusYears(minimumAge).isAfter(DateTime.now.toDateMidnight)
    } catch {
      case ex: Exception => false
    }
  }

  /*
  Is the citizen too young to vote in Scotland?
   */
  def isTooYoungToRegisterScottish(dateOfBirth: DOB) = {
    try {
      parseToDateMidnight(dateOfBirth).plusYears(minimumScotAge).isAfter(DateTime.now.toDateMidnight)
    } catch {
      case ex: Exception => false
    }
  }

  /*
  Is the citizen a valid young Scottish voter (ie. 14 or 15 years old)?
   */
  def isValidYoungScottishVoter(dateOfBirth: DOB) = {
    try {
      val fourteenYearsAgo = DateTime.now.minusYears(14).toDateMidnight
      val sixteenYearsAgo = DateTime.now.minusYears(16).toDateMidnight
      val dob = parseToDateMidnight(dateOfBirth)
      (dob.isBefore(fourteenYearsAgo) || dob.isEqual(fourteenYearsAgo)) && dob.isAfter(sixteenYearsAgo)
    } catch {
      case ex: Exception => false
    }
  }

  /*
  Is the citizen aged 14 precisely?
   */
  def is14(dateOfBirth: DOB) = {
    try {
      val fourteenYearsAgo = DateTime.now.minusYears(14).toDateMidnight
      val fifteenYearsAgo = DateTime.now.minusYears(15).toDateMidnight
      val dob = parseToDateMidnight(dateOfBirth)
      (dob.isBefore(fourteenYearsAgo) || dob.isEqual(fourteenYearsAgo)) && dob.isAfter(fifteenYearsAgo)
    } catch {
      case ex: Exception => false
    }
  }

  /*
  Is the citizen aged 15 precisely?
   */
  def is15(dateOfBirth: DOB) = {
    try {
      val fifteenYearsAgo = DateTime.now.minusYears(15).toDateMidnight
      val sixteenYearsAgo = DateTime.now.minusYears(16).toDateMidnight
      val dob = parseToDateMidnight(dateOfBirth)
      (dob.isBefore(fifteenYearsAgo) || dob.isEqual(fifteenYearsAgo)) && dob.isAfter(sixteenYearsAgo)
    } catch {
      case ex: Exception => false
    }
  }

  def dateLeftUkOver15Years(dateLeftUk:DateLeft):Boolean = {
    val leftUk = new DateTime().withMonthOfYear(dateLeftUk.month).withYear(dateLeftUk.year)
    val monthDiff = Months.monthsBetween(leftUk, DateTime.now()).getMonths()
    if (monthDiff >= 15 * 12) true
    else false
  }
  
  def isLessEighteen(dateOfBirth: DOB) = {
    try {
      val eighteenYearsAgo = DateTime.now.minusYears(18).toDateMidnight
    	val dob = parseToDateMidnight(dateOfBirth)
      dob.isAfter(eighteenYearsAgo) || dob.isEqual(eighteenYearsAgo)
    } catch {
      case ex: Exception => false
    }
  }

  private def isDateBefore(date: DateMidnight, yearsBack: Int) = {
    try {
      date.plusYears(yearsBack).isBefore(DateTime.now.toDateMidnight.plusDays(1))
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
