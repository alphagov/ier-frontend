package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{PreviousName, InprogressForces, Name}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NameConstraints

trait NameForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  lazy val nameMapping = mapping(
    keys.firstName.key -> required(text, "Please enter your first name"),
    keys.middleNames.key -> optional(nonEmptyText),
    keys.lastName.key -> required(text, "Please enter your last name")
  ) (
    Name.apply
  ) (
    Name.unapply
  ).verifying(firstNameNotTooLong, middleNamesNotTooLong, lastNameNotTooLong)

  lazy val previousNameMapping = mapping(
    keys.hasPreviousName.key -> boolean,
    keys.previousName.key -> optional(nameMapping)
  ) (
    PreviousName.apply
  ) (
    PreviousName.unapply
  ) verifying prevNameFilledIfHasPrevIsTrue

  val nameForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(nameMapping).verifying(nameNotOptional),
      keys.previousName.key -> required(optional(previousNameMapping), "Please answer this question")
    ) (
      (name, previousName) => InprogressForces(name = name, previousName = previousName)
    ) (
      inprogress => Some(inprogress.name, inprogress.previousName)
    )
  )
}
