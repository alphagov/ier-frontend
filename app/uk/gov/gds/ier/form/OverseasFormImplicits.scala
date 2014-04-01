package uk.gov.gds.ier.form

import org.joda.time.{LocalDate, YearMonth, Years}
import uk.gov.gds.ier.validation.{ErrorTransformForm, FormKeys, Key}
import uk.gov.gds.ier.model.{DateLeft, InprogressOverseas, ApplicationType, LastRegisteredType}
import scala.util.Try
import uk.gov.gds.ier.validation.constants.DateOfBirthConstants

trait OverseasFormImplicits {
  self: FormKeys =>

  implicit class OverseasImprovedForm(form:ErrorTransformForm[InprogressOverseas]) {

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

    def dateLeftSpecial = {
      for (
        month <- form(keys.dateLeftSpecial.month).value;
        year <- form(keys.dateLeftSpecial.year).value
      ) yield {
        new YearMonth().withYear(year.toInt).withMonthOfYear(month.toInt)
      }
    }

    def dateLeftUk = {
      for (
        month <- form(keys.dateLeftUk.month).value;
        year <- form(keys.dateLeftUk.year).value
      ) yield {
        new YearMonth().withYear(year.toInt).withMonthOfYear(month.toInt)
      }
    }

    def within15YearLimit = {
      val fifteenYearsAgo = new YearMonth().minusYears(15)
      dateLeftUk map { date =>
        date isAfter fifteenYearsAgo
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
        dob isBefore DateOfBirthConstants.jan1st1983
      }
    }

    def previouslyRegisteredOverseas = {
      form(keys.previouslyRegistered.hasPreviouslyRegistered).value.map { _ == "true" }
    }

    def under18WhenLeft = {
      for(dob <- dateOfBirth; whenLeft <- dateLeftUk) yield {
        Years.yearsBetween(new YearMonth(dob), whenLeft).getYears() < 18
      }
    }

    def lastRegisteredType = {
      Try {
        form(keys.lastRegisteredToVote.registeredType).value.map { regType =>
          LastRegisteredType.parse(regType)
        }
      }.getOrElse(None)
    }

    def identifyApplication:ApplicationType = {
      identifyOverseasApplication(
        dateOfBirth map { dateTime => new YearMonth(dateTime) },
        dateLeftUk,
        previouslyRegisteredOverseas,
        lastRegisteredType
      )
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
