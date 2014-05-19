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
      prevLastNameNotTooLong
    )
  )
}

trait NameConstraints extends CommonConstraints with FormKeys {

  lazy val nameRequired = Constraint[InprogressOrdinary](keys.name.key) {
    _.name match {
      case Some(_) => Valid
      case None => Invalid(
        "Please enter your full name",
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
        "Please answer this question",
        keys.previousName
      )
    }
  }

  lazy val lastNameRequired = Constraint[InprogressOrdinary] (
    keys.name.lastName.key
  ) {
    _.name match {
      case Some(Name(_, _, "")) => Invalid (
        "Please enter your last name",
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
        "Please enter your first name",
        keys.name.firstName
      )
      case _ => Valid
    }
  }

  lazy val prevFirstNameRequired = Constraint[InprogressOrdinary] (
    keys.previousName.previousName.firstName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, Some(Name("", _, _)))) => Invalid (
        "Please enter your first name",
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
        "Please enter your last name",
        keys.previousName.previousName.lastName
      )
      case _ => Valid
    }
  }

  lazy val firstNameNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.name.firstName, firstNameMaxLengthError
  ) {
    _.name map { _.firstName } getOrElse ""
  }

  lazy val middleNamesNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.name.middleNames, middleNameMaxLengthError
  ) {
    _.name flatMap { _.middleNames } getOrElse ""
  }

  lazy val lastNameNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.name.lastName, lastNameMaxLengthError
  ) {
    _.name map { _.lastName } getOrElse ""
  }

  lazy val prevFirstNameNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.previousName.previousName.firstName, firstNameMaxLengthError
  ) {
    _.previousName flatMap { _.previousName } map { _.firstName } getOrElse ""
  }

  lazy val prevMiddleNamesNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.previousName.previousName.middleNames, middleNameMaxLengthError
  ) {
    _.previousName flatMap { _.previousName } flatMap { _.middleNames } getOrElse ""
  }

  lazy val prevLastNameNotTooLong = fieldNotTooLong[InprogressOrdinary] (
    keys.previousName.previousName.lastName, lastNameMaxLengthError
  ) {
    _.previousName flatMap { _.previousName } map { _.lastName } getOrElse ""
  }
}
