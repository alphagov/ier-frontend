package uk.gov.gds.ier.transaction.ordinary.name

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{Name, PreviousName}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait NameForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val nameForm = ErrorTransformForm(
    mapping (
      keys.name.key -> optional(Name.mapping),
      keys.previousName.key -> optional(PreviousName.mapping)
    ) (
      (name, previousName) => InprogressOrdinary(
        name = name,
        previousName = previousName
      )
    ) (
      inprogress => Some(
        inprogress.name,
        inprogress.previousName
      )
    ) verifying (
      nameRequired,
      firstNameRequired,
      lastNameRequired,
      firstNameNotTooLong,
      middleNamesNotTooLong,
      lastNameNotTooLong,
      previousNameRequired,
      prevFirstNameRequired,
      prevLastNameRequired,
      prevFirstNameNotTooLong,
      prevMiddleNamesNotTooLong,
      prevLastNameNotTooLong,
      prevNameRequiredIfHasPrevNameTrue
    )
  )
}

trait NameConstraints extends CommonConstraints with FormKeys {

  lazy val nameRequired = Constraint[InprogressOrdinary](keys.name.key) {
    _.name match {
      case Some(_) => Valid
      case None => Invalid(
        "ordinary_name_error_enterFullName",
        keys.name.firstName,
        keys.name.lastName
      )
    }
  }

  lazy val previousNameRequired = Constraint[InprogressOrdinary] (
    keys.previousName.key
  ) {
    _.previousName match {
      case Some(_) => Valid
      case _ => Invalid (
        "ordinary_previousName_error_answerThis",
        keys.previousName
      )
    }
  }

  lazy val lastNameRequired = Constraint[InprogressOrdinary] (
    keys.name.lastName.key
  ) {
    _.name match {
      case Some(Name(_, _, "")) => Invalid (
        "ordinary_name_error_enterLastName",
        keys.name.lastName
      )
      case _ => Valid
    }
  }

  lazy val firstNameRequired = Constraint[InprogressOrdinary] (
    keys.name.firstName.key
  ) {
    _.name match {
      case Some(Name("", _, _)) => Invalid (
        "ordinary_name_error_enterFirstName",
        keys.name.firstName
      )
      case _ => Valid
    }
  }

  lazy val prevNameRequiredIfHasPrevNameTrue = Constraint[InprogressOrdinary] (
    keys.previousName.previousName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, None)) => Invalid (
        "ordinary_previousName_error_enterFullName",
        keys.previousName.previousName,
        keys.previousName.previousName.firstName,
        keys.previousName.previousName.lastName
      )
      case _ => Valid
    }
  }

  lazy val prevFirstNameRequired = Constraint[InprogressOrdinary] (
    keys.previousName.previousName.firstName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, Some(Name("", _, _)))) => Invalid (
        "ordinary_previousName_error_enterFirstName",
        keys.previousName.previousName.firstName
      )
      case _ => Valid
    }
  }

  lazy val prevLastNameRequired = Constraint[InprogressOrdinary] (
    keys.previousName.previousName.lastName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, Some(Name(_, _, "")))) => Invalid (
        "ordinary_previousName_error_enterLastName",
        keys.previousName.previousName.lastName
      )
      case _ => Valid
    }
  }

  lazy val firstNameNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.name.firstName,
    "ordinary_name_error_firstNameTooLong"
  ) {
    _.name map { _.firstName } getOrElse ""
  }

  lazy val middleNamesNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.name.middleNames,
    "ordinary_name_error_middleNamesTooLong"
  ) {
    _.name flatMap { _.middleNames } getOrElse ""
  }

  lazy val lastNameNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.name.lastName,
    "ordinary_name_error_lastNameTooLong"
  ) {
    _.name map { _.lastName } getOrElse ""
  }

  lazy val prevFirstNameNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.previousName.previousName.firstName,
    "ordinary_previousName_error_firstNameTooLong"
  ) {
    _.previousName flatMap { _.previousName } map { _.firstName } getOrElse ""
  }

  lazy val prevMiddleNamesNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.previousName.previousName.middleNames,
    "ordinary_previousName_error_middleNamesTooLong"
  ) {
    _.previousName flatMap { _.previousName } flatMap { _.middleNames } getOrElse ""
  }

  lazy val prevLastNameNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.previousName.previousName.lastName,
    "ordinary_previousName_error_lastNameTooLong"
  ) {
    _.previousName flatMap { _.previousName } map { _.lastName } getOrElse ""
  }
}
