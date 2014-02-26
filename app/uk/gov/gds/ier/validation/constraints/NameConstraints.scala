package uk.gov.gds.ier.validation.constraints

import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, Key}
import uk.gov.gds.ier.model.{Name, PreviousName, InprogressApplication}
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Mapping
import play.api.data.Forms._

trait NameConstraints extends CommonConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val nameNotOptional = Constraint[Option[Name]](keys.name.key) {
    name =>
      if (name.isDefined) Valid
      else Invalid("Please enter your full name", keys.name.firstName, keys.name.lastName)
  }
  
  lazy val firstNameNotTooLong = fieldNotTooLong[Name](keys.name.firstName,
    firstNameMaxLengthError) {
    name => name.firstName
  }

  lazy val middleNamesNotTooLong = fieldNotTooLong[Name](keys.name.middleNames,
    middleNameMaxLengthError) {
    name => name.middleNames.getOrElse("")
  }

  lazy val lastNameNotTooLong = fieldNotTooLong[Name](keys.name.lastName,
    lastNameMaxLengthError) {
    name => name.lastName
  }

  lazy val prevFirstNameNotTooLong = fieldNotTooLong[Name](
    keys.previousName.previousName.firstName,
    firstNameMaxLengthError) {
    name => name.firstName
  }

  lazy val prevMiddleNamesNotTooLong = fieldNotTooLong[Name](
    keys.previousName.previousName.middleNames,
    middleNameMaxLengthError) {
    name => name.middleNames.getOrElse("")
  }

  lazy val prevLastNameNotTooLong = fieldNotTooLong[Name](
    keys.previousName.previousName.lastName,
    lastNameMaxLengthError) {
    name => name.lastName
  }

  lazy val prevNameFilledIfHasPrevIsTrue = Constraint[PreviousName](keys.previousName.previousName.key) {
    prevName =>
      if ((prevName.hasPreviousName && prevName.previousName.isDefined) || !prevName.hasPreviousName){
        Valid
      } else {
        Invalid("Please enter your previous name", 
            keys.previousName.previousName.firstName, 
            keys.previousName.previousName.lastName)
      }
  }
}
