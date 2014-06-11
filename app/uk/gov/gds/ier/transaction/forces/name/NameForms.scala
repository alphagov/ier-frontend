package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.forces.InprogressForces

trait NameForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val nameForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(Name.mapping),
      keys.previousName.key -> optional(PreviousName.mapping)
    ) (
      (name, previousName) => InprogressForces(
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
      previousNameAnswered,
      firstNameRequired,
      lastNameRequired,
      prevNameRequiredIfHasPrevNameTrue,
      prevFirstNameRequired,
      prevLastNameRequired,
      firstNameNotTooLong,
      middleNamesNotTooLong,
      lastNameNotTooLong,
      prevFirstNameNotTooLong,
      prevMiddleNamesNotTooLong,
      prevLastNameNotTooLong
    )
  )
}

trait NameConstraints extends CommonConstraints with FormKeys {

  lazy val nameRequired = Constraint[InprogressForces](keys.name.key) {
    _.name match {
      case Some(_) => Valid
      case None => Invalid(
        "Please enter your full name",
        keys.name.firstName,
        keys.name.lastName
      )
    }
  }

  lazy val previousNameAnswered = Constraint[InprogressForces](
    keys.previousName.key
  ) {
    _.previousName match {
      case Some(_) => Valid
      case _ => Invalid(
        "Please answer this question",
        keys.previousName
      )
    }
  }

  lazy val lastNameRequired = Constraint[InprogressForces] (
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

  lazy val firstNameRequired = Constraint[InprogressForces] (
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

  lazy val prevFirstNameRequired = Constraint[InprogressForces] (
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

  lazy val prevLastNameRequired = Constraint[InprogressForces] (
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

  lazy val prevNameRequiredIfHasPrevNameTrue = Constraint[InprogressForces] (
    keys.previousName.previousName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, None)) => Invalid (
        "Please enter your full previous name",
        keys.previousName.previousName,
        keys.previousName.previousName.firstName,
        keys.previousName.previousName.lastName
      )
      case _ => Valid
    }
  }

  lazy val firstNameNotTooLong = fieldNotTooLong[InprogressForces] (
    keys.name.firstName,
    "First name can be no longer than 256 characters"
  ) {
    _.name map { _.firstName } getOrElse ""
  }

  lazy val middleNamesNotTooLong = fieldNotTooLong[InprogressForces] (
    keys.name.middleNames,
    "Middle names can be no longer than 256 characters"
  ) {
    _.name flatMap { _.middleNames } getOrElse ""
  }

  lazy val lastNameNotTooLong = fieldNotTooLong[InprogressForces] (
    keys.name.lastName,
    "Last name can be no longer than 256 characters"
  ) {
    _.name map { _.lastName } getOrElse ""
  }

  lazy val prevFirstNameNotTooLong = fieldNotTooLong[InprogressForces] (
    keys.previousName.previousName.firstName,
    "Previous first name can be no longer than 256 characters"
  ) {
    _.previousName flatMap { _.previousName } map { _.firstName } getOrElse ""
  }

  lazy val prevMiddleNamesNotTooLong = fieldNotTooLong[InprogressForces] (
    keys.previousName.previousName.middleNames,
    "Previous middle names can be no longer than 256 characters"
  ) {
    _.previousName flatMap { _.previousName } flatMap { _.middleNames } getOrElse ""
  }

  lazy val prevLastNameNotTooLong = fieldNotTooLong[InprogressForces] (
    keys.previousName.previousName.lastName,
    "Previous last name can be no longer than 256 characters"
  ) {
    _.previousName flatMap { _.previousName } map { _.lastName } getOrElse ""
  }
}

