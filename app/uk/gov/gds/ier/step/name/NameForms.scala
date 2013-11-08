package uk.gov.gds.ier.step.name

import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressApplication, Name}
import play.api.data.Form
import play.api.data.Forms._

trait NameForms {
  self:  FormKeys
    with ErrorMessages =>

  lazy val nameMapping = mapping(
    keys.firstName.key -> optional(text.verifying(firstNameMaxLengthError, _.size <= maxTextFieldLength))
      .verifying("Please enter your first name", _.nonEmpty),
    keys.middleNames.key -> optional(nonEmptyText.verifying(middleNameMaxLengthError, _.size <= maxTextFieldLength)),
    keys.lastName.key -> optional(text.verifying(lastNameMaxLengthError, _.size <= maxTextFieldLength))
      .verifying("Please enter your last name", _.nonEmpty)
  ) (
    (firstName, middleName, lastName) => Name(firstName.get, middleName, lastName.get)
  ) (
    name => Some(Some(name.firstName), name.middleNames, Some(name.lastName))
  )

  val nameForm = Form(
    mapping(
      keys.name.key -> optional(nameMapping)
        .verifying("Please enter your full name", _.isDefined)
    ) (
      name => InprogressApplication(name = name)
    ) (
      inprogress => Some(inprogress.name)
    )
  )
}
