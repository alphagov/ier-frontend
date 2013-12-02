package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{DateValidator, FormKeys, ErrorMessages}
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{InprogressApplication, DOB}

trait DateOfBirthConstraints {
  self: ErrorMessages
    with FormKeys =>

  lazy val dateOfBirthRequired = Constraint[InprogressApplication](keys.dob.key) {
    application => application.dob match {
      case Some(dob) => Valid
      case None => Invalid(
        "Please enter your date of birth", 
        keys.dob.dob.day, 
        keys.dob.dob.month, 
        keys.dob.dob.year
      )
    }
  }

  lazy val isOverTheMinimumAgeToVote = Constraint[DOB](keys.dob.key) {
    dateOfBirth =>
      if (DateValidator.isExistingDateInThePast(dateOfBirth) && 
          DateValidator.isTooYoungToRegister(dateOfBirth)) {
        Invalid(
          s"Minimum age to register to vote is ${DateValidator.minimumAge}",
          keys.dob.dob.day,
          keys.dob.dob.month,
          keys.dob.dob.year
        )
      } else {
        Valid
      }
  }

  lazy val dateNotInTheFuture = Constraint[DOB](keys.dob.key) {
    dateOfBirth =>
      if (DateValidator.isExistingDateInThePast(dateOfBirth)) {
        Valid
      } else {
        Invalid(
          "You have entered a date in the future", 
          keys.dob.dob.day,
          keys.dob.dob.month,
          keys.dob.dob.year
        )
      }
  }

  lazy val notTooOldToBeAlive = Constraint[DOB](keys.dob.key) {
    dateOfBirth =>
      if (DateValidator.isTooOldToBeAlive(dateOfBirth)) {
        Invalid("Please check the year you were born", keys.dob.dob.year)
      } else {
        Valid
      }
  }
}
