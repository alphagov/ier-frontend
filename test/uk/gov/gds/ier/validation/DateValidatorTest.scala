package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.DOB
import org.scalatest.{Matchers, FlatSpec}
import org.joda.time.{DateMidnight, DateTime}

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class DateValidatorTest 
  extends FlatSpec 
  with Matchers {

  behavior of "DateValidator.isExistingDateInThePast"
  it should "return true for an existing past date" in {
    DateValidator.isExistingDateInThePast(DOB(1986, 10, 11)) should be(true)  
  }

  it should "return true for today" in {
    val now = DateTime.now.toDateMidnight
    DateValidator.isExistingDateInThePast(getDateOfBirth(now)) should be(true)  
  }

  it should "return false for a non-existing past date" in {
    DateValidator.isExistingDateInThePast(DOB(1987, 2, 29)) should be(false)  
  }

  it should "return false for a future past date" in {
    val tomorrow = DateTime.now.toDateMidnight.plusDays(1)
    DateValidator.isExistingDateInThePast(getDateOfBirth(tomorrow)) should be(false)
  }

  behavior of "DateValidator.isTooOldToBeAlive"

  it should "return false for a date newer than 115 years ago" in {
    val almost115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).plusDays(1)
    DateValidator.isTooOldToBeAlive(getDateOfBirth(almost115yearsAgo)) should be(false)
  }

  it should "return true for a date equal to 115 years ago" in {
    val exactly115yearsAgo = DateTime.now.toDateMidnight.minusYears(115)
    DateValidator.isTooOldToBeAlive(getDateOfBirth(exactly115yearsAgo)) should be(true)
  }

  it should "return true for a date older than 115 years ago" in {
    val moreThan115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).minusDays(1)
    DateValidator.isTooOldToBeAlive(getDateOfBirth(moreThan115yearsAgo)) should be(true)
  }
 
  behavior of "DateValidator.isTooYoungToRegister"

  it should "return false for a date older than 16 years ago" in {
    val moreThan16YearsAgo = DateTime.now.toDateMidnight.minusYears(16).minusDays(1)
    DateValidator.isTooYoungToRegister(getDateOfBirth(moreThan16YearsAgo)) should be(false)  
  }

  it should "return false for a date equal to 16 years ago" in {
    val moreThan16YearsAgo = DateTime.now.toDateMidnight.minusYears(16)
    DateValidator.isTooYoungToRegister(getDateOfBirth(moreThan16YearsAgo)) should be(false)
  }

  it should "return true for a date newer than 16 years ago" in {
    val almost16YearsAgo = DateTime.now.toDateMidnight.minusYears(16).plusDays(1)
    DateValidator.isTooYoungToRegister(getDateOfBirth(almost16YearsAgo)) should be(true)
  }

  private def getDateOfBirth(date: DateMidnight) = DOB(date.getYear, date.getMonthOfYear, date.getDayOfMonth)
}
