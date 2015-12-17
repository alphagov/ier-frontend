package uk.gov.gds.ier.transaction.overseas.previousName

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PreviousName, Name}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NameCommonConstraints
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

trait PreviousNameForms extends PreviousNameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val previousNameForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(Name.mapping)
        .verifying(
        firstNameNotTooLong,
        middleNamesNotTooLong,
        lastNameNotTooLong),
      keys.previousName.key -> optional(PreviousName.mapping)
        .verifying(
        prevFirstNameNotTooLong,
        prevMiddleNamesNotTooLong,
        prevLastNameNotTooLong)
    ) (
      (name, previousName) => InprogressOverseas(
        name = name,
        previousName = previousName
      )
    ) (
      inprogress => Some(
        inprogress.name,
        inprogress.previousName
      )
    ) verifying (
      prevNameRequiredIfHasPrevNameTrue,
      prevFirstNameRequired,
      prevLastNameRequired,
      prevReasonRequired
      )
  )
}

trait PreviousNameConstraints extends NameCommonConstraints with FormKeys {

  lazy val prevFirstNameRequired = Constraint[InprogressOverseas] (
    keys.previousName.previousName.firstName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, "true", _, Some(Name("", _, _)), _)) => Invalid (
        "Please enter your previous first name",
        keys.previousName.previousName.firstName
      )
      case _ => Valid
    }
  }

  lazy val prevLastNameRequired = Constraint[InprogressOverseas] (
    keys.previousName.previousName.lastName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, "true", _, Some(Name(_, _, "")), _)) => Invalid (
        "Please enter your previous last name",
        keys.previousName.previousName.lastName
      )
      case _ => Valid
    }
  }

  lazy val prevReasonRequired = Constraint[InprogressOverseas] (
    keys.previousName.reason.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, "true", _, _, reason)) if reason.isEmpty || reason.exists(_.isEmpty) => Invalid(
          "Please provide a reason for changing your name",
          keys.previousName.reason)
      case _ => Valid
    }
  }

  lazy val prevNameRequiredIfHasPrevNameTrue = Constraint[InprogressOverseas] (
    keys.previousName.previousName.key
  ) {
    _.previousName match {
      case Some(PreviousName(true, "true", _, None, _)) => Invalid (
        "Please enter your full previous name",
        keys.previousName.previousName,
        keys.previousName.previousName.firstName,
        keys.previousName.previousName.lastName
      )
      case _ => Valid
    }
  }

}