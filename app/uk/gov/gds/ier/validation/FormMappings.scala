package uk.gov.gds.ier.validation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation
import uk.gov.gds.ier.validation.DateValidator._

trait FormMappings extends FormKeys {

  private final val maxTextFieldLength = 256
  private final val maxExplanationFieldLength = 500

  val nameMapping = mapping(
    firstName -> text.verifying("Please enter your first name", _.nonEmpty).verifying(firstNameMaxLengthError, _.size <= maxTextFieldLength),
    middleNames -> optional(nonEmptyText.verifying(middleNameMaxLengthError, _.size <= maxTextFieldLength)),
    lastName -> text.verifying("Please enter your last name", _.nonEmpty).verifying(lastNameMaxLengthError, _.size <= maxTextFieldLength)
  ) (Name.apply) (Name.unapply)

  val previousNameMapping = mapping(
    hasPreviousName -> boolean,
    previousName -> optional(nameMapping)
  ) (PreviousName.apply) (PreviousName.unapply)

  val contactMapping = mapping(
    contactType -> optional(text).verifying("Please answer this question", contact => contact.nonEmpty && List("text", "phone", "email", "post").contains(contact.get)),
    post -> optional(nonEmptyText.verifying(postMaxLengthError, _.size <= maxTextFieldLength)),
    phone -> optional(nonEmptyText.verifying(phoneMaxLengthError, _.size <= maxTextFieldLength)),
    textNum -> optional(nonEmptyText.verifying(textNumMaxLengthError, _.size <= maxTextFieldLength)),
    email -> optional(nonEmptyText.verifying(emailMaxLengthError, _.size <= maxTextFieldLength))
  ) { (contactType, post, phone, textNum, email) => Contact(contactType.get, post, phone, textNum, email) }
  { contact => Some(Some(contact.contactType), contact.post, contact.phone, contact.textNum, contact.email) }

  val nationalityMapping = mapping(
    nationalities -> list(nonEmptyText.verifying(nationalityMaxLengthError, _.size <= maxTextFieldLength)),
    otherCountries -> list(nonEmptyText.verifying(nationalityMaxLengthError, _.size <= maxTextFieldLength)),
    noNationalityReason -> optional(nonEmptyText.verifying(noNationalityReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (Nationality.apply) (Nationality.unapply) verifying("Please select your Nationality", nationality => {
    (nationality.nationalities.size > 0 || nationality.otherCountries.size > 0) || nationality.noNationalityReason.isDefined
  }) verifying("You can specify no more than five countries", mapping => mapping.nationalities.size + mapping.otherCountries.size <=5)

  val addressMapping = mapping(
    address -> nonEmptyText.verifying(addressMaxLengthError, _.size <= maxTextFieldLength).verifying("Please select your address", address => address != "Select your address"),
    postcode -> nonEmptyText.verifying("Your postcode is not valid", postcode => PostcodeValidator.isValid(postcode))
  ) (Address.apply) (Address.unapply)

  val previousAddressMapping = mapping(
    movedRecently -> boolean,
    previousAddress -> optional(addressMapping)
  ) (PreviousAddress.apply) (PreviousAddress.unapply)

  val otherAddressMapping = mapping(
    hasOtherAddress -> boolean
  ) (OtherAddress.apply) (OtherAddress.unapply)

  val optInMapping = single(
    optIn -> boolean
  )

  val dobMapping = mapping(
    year -> text.verifying("Please enter your year of birth", _.nonEmpty).verifying("The year you provided is invalid", year => year.isEmpty || year.matches("\\d+")),
    month -> text.verifying("Please enter your month of birth", _.nonEmpty).verifying("The month you provided is invalid", month => month.isEmpty || month.matches("\\d+")),
    day -> text.verifying("Please enter you day of birth", _.nonEmpty).verifying("The day you provided is invalid", day => day.isEmpty || day.matches("\\d+"))
  ) {
    (year, month, day) => DateOfBirth(year.toInt, month.toInt, day.toInt)
  } {
    dateOfBirth => Some(dateOfBirth.year.toString, dateOfBirth.month.toString, dateOfBirth.day.toString)
  } verifying(
    "The date you specified is invalid", dob => isExistingDateInThePast(dob) && !isTooOldToBeAlive(dob)
    ) verifying(
    "Minimum age to register to vote is %d".format(minimumAge), dob => !isExistingDateInThePast(dob) || !isTooYoungToRegister(dob)
    )

  val ninoMapping = mapping(
    nino -> optional(nonEmptyText.verifying("Your National Insurance number is not correct", nino => NinoValidator.isValid(nino))),
    noNinoReason -> optional(nonEmptyText.verifying(noNinoReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (Nino.apply) (Nino.unapply)

  private def firstNameMaxLengthError = "First name can be no longer than %s characters".format(maxTextFieldLength)
  private def middleNameMaxLengthError = "Middle names can be no longer than %s characters".format(maxTextFieldLength)
  private def lastNameMaxLengthError = "Last name can be no longer than %s characters".format(maxTextFieldLength)
  private def postMaxLengthError = "Post information can be no longer than %s characters".format(maxTextFieldLength)
  private def phoneMaxLengthError = "Phone number can be no longer than %s characters".format(maxTextFieldLength)
  private def textNumMaxLengthError = "Phone number for text contact can be no longer than %s characters".format(maxTextFieldLength)
  private def emailMaxLengthError = "Email address can be no longer than %s characters".format(maxTextFieldLength)
  private def nationalityMaxLengthError = "Country name can be no longer than %s characters".format(maxTextFieldLength)
  private def noNationalityReasonMaxLengthError = "Reason for not providing nationality must be described in up to %s characters".format(maxExplanationFieldLength)
  private def addressMaxLengthError = "Address information should be no longer than %s characters".format(maxTextFieldLength)
  private def noNinoReasonMaxLengthError = "Reason for not providing National Insurance number must be described in up to %s characters".format(maxExplanationFieldLength)
}
