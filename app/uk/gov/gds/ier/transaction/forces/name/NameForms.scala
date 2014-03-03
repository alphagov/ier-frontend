package uk.gov.gds.ier.transaction.forces.name

import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressForces, Name}
import play.api.data.Forms._
import uk.gov.gds.ier.validation.constraints.NameConstraints

trait NameForms extends NameConstraints {
  self:  FormKeys
    with ErrorMessages =>

  private lazy val generalNameMapping = mapping(
    keys.firstName.key -> required(text, "Please enter your first name"),
    keys.middleNames.key -> optional(nonEmptyText),
    keys.lastName.key -> required(text, "Please enter your last name")
  ) (
    Name.apply
  ) (
    Name.unapply
  )

  lazy val nameMapping = generalNameMapping verifying(
    firstNameNotTooLong, middleNamesNotTooLong, lastNameNotTooLong)

  val nameForm = ErrorTransformForm(
    mapping(
      keys.name.key -> optional(nameMapping).verifying(nameNotOptional)
    ) (
      (name) => InprogressForces(name = name)
    ) (
      inprogress => Some(inprogress.name)
    )
  )
}
