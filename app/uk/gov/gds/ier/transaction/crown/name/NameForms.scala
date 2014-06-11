package uk.gov.gds.ier.transaction.crown.name

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import uk.gov.gds.ier.transaction.crown.InprogressCrown

trait NameForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val nameForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(Name.mapping),
      keys.previousName.key -> optional(PreviousName.mapping)
    ) (
      (name, previousName) => InprogressCrown(
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

  lazy val nameRequired = Constraint[InprogressCrown](keys.name.key) {
    _.name match {
      case Some(_) => Valid
      case None => Invalid(
        "Please enter your full name",
        keys.name.firstName,
        keys.name.lastName
      )
    }
  }

  lazy val previousNameAnswered = Constraint[InprogressCrown](
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

  lazy val lastNameRequired = Constraint[InprogressCrown] (
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

  lazy val firstNameRequired = Constraint[InprogressCrown] (
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

  lazy val prevFirstNameRequired = Constraint[InprogressCrown] (
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

  lazy val prevLastNameRequired = Constraint[InprogressCrown] (
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

  lazy val prevNameRequiredIfHasPrevNameTrue = Constraint[InprogressCrown] (
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

  lazy val firstNameNotTooLong = fieldNotTooLong[InprogressCrown] (
    keys.name.firstName,
    "First name can be no longer than 256 characters"
  ) {
    _.name map { _.firstName } getOrElse ""
  }

  lazy val middleNamesNotTooLong = fieldNotTooLong[InprogressCrown] (
    keys.name.middleNames,
    "Middle names can be no longer than 256 characters"
  ) {
    _.name flatMap { _.middleNames } getOrElse ""
  }

  lazy val lastNameNotTooLong = fieldNotTooLong[InprogressCrown] (
    keys.name.lastName,
    "Last name can be no longer than 256 characters"
  ) {
    _.name map { _.lastName } getOrElse ""
  }

  lazy val prevFirstNameNotTooLong = fieldNotTooLong[InprogressCrown] (
    keys.previousName.previousName.firstName,
    "Previous first name can be no longer than 256 characters"
  ) {
    _.previousName flatMap { _.previousName } map { _.firstName } getOrElse ""
  }

  lazy val prevMiddleNamesNotTooLong = fieldNotTooLong[InprogressCrown] (
    keys.previousName.previousName.middleNames,
    "Previous middle names can be no longer than 256 characters"
  ) {
    _.previousName flatMap { _.previousName } flatMap { _.middleNames } getOrElse ""
  }

  lazy val prevLastNameNotTooLong = fieldNotTooLong[InprogressCrown] (
    keys.previousName.previousName.lastName,
    "Previous last name can be no longer than 256 characters"
  ) {
    _.previousName flatMap { _.previousName } map { _.lastName } getOrElse ""
  }
}
