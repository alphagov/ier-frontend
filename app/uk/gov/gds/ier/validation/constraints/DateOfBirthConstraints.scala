package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation._
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.{InprogressApplication, DateOfBirth, DOB, noDOB}

trait DateOfBirthConstraints extends CommonConstraints{
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

  lazy val ifDobEmptyRangeIsValid = Constraint[DateOfBirth](keys.noDob.key) {
    case DateOfBirth(Some(dob), _) => {
      Valid
    }
    case DateOfBirth(None, None) => {
      Valid
    }
    case DateOfBirth(_, Some(noDob)) => {
      if (noDob.range.exists(DateOfBirthConstants.noDobRanges.contains)) {
        Valid
      } else {
        Invalid("Please select a rough age range", keys.dob.noDob.range)
      }
    }
  }

  lazy val ifDobEmptyReasonIsNotEmpty = Constraint[DateOfBirth](keys.noDob.key) {
    case DateOfBirth(Some(dob), _) => {
      Valid
    }
    case DateOfBirth(None, None) => {
      Valid
    }
    case DateOfBirth(_, Some(noDob)) => {
      if (noDob.reason.exists(!_.isEmpty)) {
        Valid
      } else {
        Invalid("Please provide a reason", keys.dob.noDob.reason)
      }
    }
  }

  lazy val dobOrNoDobIsFilled = Constraint[DateOfBirth](keys.dob.key) {
    dateOfBirth => 
      if (dateOfBirth.dob.isDefined || dateOfBirth.noDob.isDefined) {
        Valid
      } else {
        Invalid("Please answer this question", keys.dob.dob)
      }
  }
}
