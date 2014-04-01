package uk.gov.gds.ier.validation

import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.model.PartialManualAddress

trait ErrorMessages {
  lazy val maxTextFieldLength = 256
  lazy val maxExplanationFieldLength = 500

  lazy val firstNameMaxLengthError = s"First name can be no longer than $maxTextFieldLength characters"
  lazy val middleNameMaxLengthError = s"Middle names can be no longer than $maxTextFieldLength characters"
  lazy val lastNameMaxLengthError = s"Last name can be no longer than $maxTextFieldLength characters"
  lazy val postMaxLengthError = s"Post information can be no longer than $maxTextFieldLength characters"
  lazy val phoneMaxLengthError = s"Phone number can be no longer than $maxTextFieldLength characters"
  lazy val textNumMaxLengthError = s"Phone number for text contact can be no longer than $maxTextFieldLength characters"
  lazy val emailMaxLengthError = s"Email address can be no longer than $maxTextFieldLength characters"
  lazy val nationalityMaxLengthError = s"Country name can be no longer than $maxTextFieldLength characters"
  lazy val noNationalityReasonMaxLengthError = s"Reason for not providing nationality must be described in up to $maxTextFieldLength characters"
  lazy val addressMaxLengthError = s"Address information should be no longer than $maxTextFieldLength characters"
  lazy val noNinoReasonMaxLengthError = s"Reason for not providing National Insurance number must be described in up to $maxTextFieldLength characters"

  lazy val lineOneIsRequiredError = s"At least one address line is required"
  lazy val cityIsRequiredError = s"Postal town or city is required"
}
