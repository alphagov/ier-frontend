package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{DateValidator, FormKeys, ErrorMessages}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{InprogressApplication, DateOfBirth}

trait DateOfBirthConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val dateOfBirthRequired = Constraint[InprogressApplication](keys.dob.key) {
    application => application.dob match {
      case Some(dob) => Valid
      case None => Invalid("Please enter your date of birth", keys.dob.day, keys.dob.month, keys.dob.year)
    }
  }

  lazy val isOverTheMinimumAgeToVote = Constraint[DateOfBirth](keys.dob.key) {
    dateOfBirth =>
      if (DateValidator.isExistingDateInThePast(dateOfBirth) && DateValidator.isTooYoungToRegister(dateOfBirth)) {
        Invalid(
          s"Minimum age to register to vote is ${DateValidator.minimumAge}",
          keys.dob.day,
          keys.dob.month,
          keys.dob.year
        )
      } else {
        Valid
      }
  }

  lazy val dateNotInTheFuture = Constraint[DateOfBirth](keys.dob.key) {
    dateOfBirth =>
      if (DateValidator.isExistingDateInThePast(dateOfBirth)) {
        Valid
      } else {
        Invalid("You have entered a date in the future", keys.dob.day, keys.dob.month, keys.dob.year)
      }
  }

  lazy val notTooOldToBeAlive = Constraint[DateOfBirth](keys.dob.key) {
    dateOfBirth =>
      if (DateValidator.isTooOldToBeAlive(dateOfBirth)) {
        Invalid("Please check the year you were born", keys.dob.year)
      } else {
        Valid
      }
  }
}
