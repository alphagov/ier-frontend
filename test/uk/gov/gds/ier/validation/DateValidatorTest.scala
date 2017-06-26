package uk.gov.gds.ier.validation

import uk.gov.gds.ier.model.DOB
import org.joda.time.{DateMidnight, DateTime}
import uk.gov.gds.ier.test.UnitTestSuite

class DateValidatorTest extends UnitTestSuite {

  behavior of "DateValidator.isExistingDateInThePast"
  it should "return true for an existing past date" in {
    DateValidator.isExistingDateInThePast(new DateMidnight(1986, 10, 11)) should be(true)
  }

  it should "return true for today" in {
    val now = DateTime.now.toDateMidnight
    DateValidator.isExistingDateInThePast(now) should be(true)
  }

  it should "return false for a non-existing date" in {
    DateValidator.isExistingDate(DOB(1978, 2, 29)) should be(None)
  }

  it should "return false for a future past date" in {
    val tomorrow = DateTime.now.toDateMidnight.plusDays(1)
    DateValidator.isExistingDateInThePast(tomorrow) should be(false)
  }

  behavior of "DateValidator.isTooOldToBeAlive"

  it should "return false for a date newer than 115 years ago" in {
    val almost115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).plusDays(1)
    DateValidator.isTooOldToBeAlive(almost115yearsAgo) should be(false)
  }

  it should "return true for a date equal to 115 years ago" in {
    val exactly115yearsAgo = DateTime.now.toDateMidnight.minusYears(115)
    DateValidator.isTooOldToBeAlive(exactly115yearsAgo) should be(true)
  }

  it should "return true for a date older than 115 years ago" in {
    val moreThan115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).minusDays(1)
    DateValidator.isTooOldToBeAlive(moreThan115yearsAgo) should be(true)
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

  it should "return true for a date less than 18 years ago" in {
    val lessThan18years = DateTime.now.toDateMidnight.minusYears(18).plusDays(1)
    DateValidator.isLessEighteen(getDateOfBirth(lessThan18years)) should be(true)
  }
  it should "return false for a date more than 18 years ago" in {
    val lessThan18years = DateTime.now.toDateMidnight.minusYears(18).minusDays(1)
    DateValidator.isLessEighteen(getDateOfBirth(lessThan18years)) should be(false)
  }

  behavior of "DateValidator.isCitizenshipTooOld"

  it should "return false for a date newer than 115 years ago" in {
    val almost115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).plusDays(1)
    DateValidator.isCitizenshipTooOld(almost115yearsAgo) should be(false)
  }

  it should "return true for a date equal to 115 years ago" in {
    val exactly115yearsAgo = DateTime.now.toDateMidnight.minusYears(115)
    DateValidator.isCitizenshipTooOld(exactly115yearsAgo) should be(true)
  }

  it should "return true for a date older than 115 years ago" in {
    val moreThan115yearsAgo = DateTime.now.toDateMidnight.minusYears(115).minusDays(1)
    DateValidator.isCitizenshipTooOld(moreThan115yearsAgo) should be(true)
  }

  behavior of "DateValidator.isTooYoungToRegisterScottish"

  it should "return true for age 13yrs 364days via DOB object" in {
    val age13and364days = DateTime.now.toDateMidnight.minusYears(13).minusDays(364)
    DateValidator.isTooYoungToRegisterScottish(getDateOfBirth(age13and364days)) should be(true)
  }

  it should "return false for age 14yrs 0day via DOB object" in {
    val age14and0days = DateTime.now.toDateMidnight.minusYears(14)
    DateValidator.isTooYoungToRegisterScottish(getDateOfBirth(age14and0days)) should be(false)
  }

  it should "return true for age 13yrs 364days via DD MM YYYY" in {
    val age13and364days_Y = DateTime.now.toDateMidnight.minusYears(13).minusDays(364).getYear
    val age13and364days_M = DateTime.now.toDateMidnight.minusYears(13).minusDays(364).getMonthOfYear
    val age13and364days_D = DateTime.now.toDateMidnight.minusYears(13).minusDays(364).getDayOfMonth
    DateValidator.isTooYoungToRegisterScottishByInt(age13and364days_Y, age13and364days_M, age13and364days_D) should be(true)
  }

  it should "return false for age 14yrs 0day via DD MM YYYY" in {
    val age14and0days_Y = DateTime.now.toDateMidnight.minusYears(14).getYear
    val age14and0days_M = DateTime.now.toDateMidnight.minusYears(14).getMonthOfYear
    val age14and0days_D = DateTime.now.toDateMidnight.minusYears(14).getDayOfMonth
    DateValidator.isTooYoungToRegisterScottishByInt(age14and0days_Y, age14and0days_M, age14and0days_D) should be(false)
  }

  behavior of "DateValidator.isValidYoungScottishVoter"

  it should "return false for age 13yrs 364days via DOB object" in {
    val age13and364days = DateTime.now.toDateMidnight.minusYears(13).minusDays(364)
    DateValidator.isValidYoungScottishVoter(getDateOfBirth(age13and364days)) should be(false)
  }

  it should "return true for age 14yrs 0days via DOB object" in {
    val age14and0days = DateTime.now.toDateMidnight.minusYears(14)
    DateValidator.isValidYoungScottishVoter(getDateOfBirth(age14and0days)) should be(true)
  }

  it should "return true for age 15yrs 364days via DOB object" in {
    val age15and364days = DateTime.now.toDateMidnight.minusYears(15).minusDays(364)
    DateValidator.isValidYoungScottishVoter(getDateOfBirth(age15and364days)) should be(true)
  }

  it should "return false for age 16yrs 0days via DOB object" in {
    val age16and0days = DateTime.now.toDateMidnight.minusYears(16)
    DateValidator.isValidYoungScottishVoter(getDateOfBirth(age16and0days)) should be(false)
  }

  private def getDateOfBirth(date: DateMidnight) = DOB(date.getYear, date.getMonthOfYear, date.getDayOfMonth)
}
