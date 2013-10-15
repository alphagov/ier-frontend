package uk.gov.gds.ier.validation

import org.specs2.mutable.Specification
import uk.gov.gds.ier.model.DateOfBirth
import org.joda.time.{DateMidnight, DateTime}

class DateValidatorTest extends Specification {

  "isExistingDateInThePast" should {
    "return true for an existing past date" in {
      DateValidator.isExistingDateInThePast(DateOfBirth(1986, 10, 11)) mustEqual(true)
    }
  }

  "isExistingDateInThePast" should {
    "return true for today" in {
      val now = DateTime.now.toDateMidnight
      DateValidator.isExistingDateInThePast(getDateOfBirth(now)) mustEqual(true)
    }
  }

  "isExistingDateInThePast" should {
    "return false for a non-existing past date" in {
      DateValidator.isExistingDateInThePast(DateOfBirth(1987, 2, 29)) mustEqual(false)
    }
  }

  "isExistingDateInThePast" should {
    "return false for a future past date" in {
      val tomorrow = DateTime.now.toDateMidnight.plusDays(1)
      DateValidator.isExistingDateInThePast(getDateOfBirth(tomorrow)) mustEqual(false)
    }
  }

  "isTooOldToBeAlive" should {
    "return false for a date newer than 115 years ago" in {
      val almost115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).plusDays(1)
      DateValidator.isTooOldToBeAlive(getDateOfBirth(almost115yearsAgo)) mustEqual(false)
    }
  }

  "isTooOldToBeAlive" should {
    "return true for a date equal to 115 years ago" in {
      val exactly115yearsAgo = DateTime.now.toDateMidnight.minusYears(115)
      DateValidator.isTooOldToBeAlive(getDateOfBirth(exactly115yearsAgo)) mustEqual(true)
    }
  }

  "isTooOldToBeAlive" should {
    "return true for a date older than 115 years ago" in {
      val moreThan115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).minusDays(1)
      DateValidator.isTooOldToBeAlive(getDateOfBirth(moreThan115yearsAgo)) mustEqual(true)
    }
  }

  "isTooYoungToRegister" should {
    "return false for a date older than 16 years ago" in {
      val moreThan16YearsAgo = DateTime.now.toDateMidnight.minusYears(16).minusDays(1)
      DateValidator.isTooYoungToRegister(getDateOfBirth(moreThan16YearsAgo)) mustEqual(false)
    }
  }

  "isTooYoungToRegister" should {
    "return false for a date equal to 16 years ago" in {
      val moreThan16YearsAgo = DateTime.now.toDateMidnight.minusYears(16)
      DateValidator.isTooYoungToRegister(getDateOfBirth(moreThan16YearsAgo)) mustEqual(false)
    }
  }

  "isTooYoungToRegister" should {
    "return true for a date newer than 16 years ago" in {
      val almost16YearsAgo = DateTime.now.toDateMidnight.minusYears(16).plusDays(1)
      DateValidator.isTooYoungToRegister(getDateOfBirth(almost16YearsAgo)) mustEqual(true)
    }
  }

  private def getDateOfBirth(date: DateMidnight) = DateOfBirth(date.getYear, date.getMonthOfYear, date.getDayOfMonth)
}
