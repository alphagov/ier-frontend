package uk.gov.gds.ier.step.name

import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressApplication, Name, PreviousName}
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NameConstraints

trait NameForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  private lazy val generalNameMapping = mapping(
    keys.firstName.key -> requiredOptional(text, "Please enter your first name"),
    keys.middleNames.key -> optional(nonEmptyText),
    keys.lastName.key -> requiredOptional(text, "Please enter your last name")
  ) (
    (firstName, middleName, lastName) => Name(firstName.get, middleName, lastName.get)
  ) (
    name => Some(Some(name.firstName), name.middleNames, Some(name.lastName))
  )

  private lazy val prevNameMapping = generalNameMapping verifying(
    firstNameNotTooLong, middleNamesNotTooLong, lastNameNotTooLong)

  lazy val nameMapping = generalNameMapping verifying(
    firstNameNotTooLong, middleNamesNotTooLong, lastNameNotTooLong)

  lazy val previousNameMapping = mapping(
    keys.hasPreviousName.key -> boolean,
    keys.previousName.key -> optional(prevNameMapping)
  ) (
    PreviousName.apply
  ) (
    PreviousName.unapply
  ) verifying prevNameFilledIfHasPrevIsTrue

  val nameForm = Form(
    mapping(
      keys.name.key -> requiredOptional(nameMapping, "Please enter your full name"),
      keys.previousName.key -> requiredOptional(previousNameMapping, "Please answer this question")
    ) (
      (name, previousName) => InprogressApplication(name = name, previousName = previousName)
    ) (
      inprogress => Some(inprogress.name, inprogress.previousName)
    )
  )
}
