package uk.gov.gds.ier.form

import org.joda.time.{LocalDate, YearMonth, Years}
import uk.gov.gds.ier.validation.FormKeys
import uk.gov.gds.ier.validation.InProgressForm
import uk.gov.gds.ier.model.{InprogressOverseas, ApplicationType, LastRegisteredType}

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

  implicit class OverseasImprovedApplication(application: InprogressOverseas) {
    def identifyApplication:ApplicationType = {
      val dateOfBirth = application.dob.map { dob =>
        new YearMonth().withYear(dob.year).withMonthOfYear(dob.month)
      }
      val whenLeft = application.dateLeftUk.map { dateLeft =>
        new YearMonth().withYear(dateLeft.year).withMonthOfYear(dateLeft.month)
      }
      val previouslyRegistered = application.previouslyRegistered.map(_.hasPreviouslyRegistered)
      val lastRegistered = application.lastRegisteredToVote.map(_.lastRegisteredType)

      identifyOverseasApplication(dateOfBirth, whenLeft, previouslyRegistered, lastRegistered)
    }
  }

  private def identifyOverseasApplication(
      dob:Option[YearMonth],
      dateLeft:Option[YearMonth],
      previouslyRegistered: Option[Boolean],
      lastRegistered: Option[LastRegisteredType]):ApplicationType = {

    val under18WhenLeft = for(dateOfBirth <- dob; whenLeft <- dateLeft) yield {
      Years.yearsBetween(dateOfBirth, whenLeft).getYears() < 18
    }

    if (previouslyRegistered.exists(_ == true)) {
      ApplicationType.RenewerVoter
    } else if (lastRegistered.exists(_ == LastRegisteredType.Ordinary)) {
      ApplicationType.NewVoter
    } else if (lastRegistered.exists(_ == LastRegisteredType.Forces)
        || lastRegistered.exists(_ == LastRegisteredType.Crown)
        || lastRegistered.exists(_ == LastRegisteredType.Council)){
      ApplicationType.SpecialVoter
    } else if (under18WhenLeft.exists(_ == true)) {
      ApplicationType.YoungVoter
    } else {
      ApplicationType.DontKnow
    }
  }
}
