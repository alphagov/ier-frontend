package uk.gov.gds.ier.form

import org.joda.time.LocalDate
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.validation.InProgressForm

trait OverseasFormImplicits {
  self: FormKeys => 
  
  implicit class OverseasImprovedForm(form:InProgressForm[InprogressOverseas]) {

    private val jan1st1983 = new LocalDate()
      .withYear(1983)
      .withMonthOfYear(1)
      .withDayOfMonth(1)

    def dateOfBirth = {
      for(
        day <- form(keys.dob.day).value;
        month <- form(keys.dob.month).value;
        year <- form(keys.dob.year).value
      ) yield {
        new LocalDate()
          .withYear(year.toInt)
          .withMonthOfYear(month.toInt)
          .withDayOfMonth(day.toInt)
      }
    }

    def dateBecameCitizen = for (
      day <- form(keys.passport.citizenDetails.dateBecameCitizen.day).value;
      month <- form(keys.passport.citizenDetails.dateBecameCitizen.month).value;
      year <- form(keys.passport.citizenDetails.dateBecameCitizen.year).value
    ) yield {
      new LocalDate()
        .withYear(year.toInt)
        .withMonthOfYear(month.toInt)
        .withDayOfMonth(day.toInt)
    }

    def bornBefore1983 = {
      dateOfBirth map { dob =>
        dob isBefore jan1st1983
      }
    }
  }
}
