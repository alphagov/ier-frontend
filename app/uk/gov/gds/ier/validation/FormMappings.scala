package uk.gov.gds.ier.validation

import play.api.data.Forms._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation
import uk.gov.gds.ier.validation.DateValidator._
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.validation.{Invalid, Valid, Constraint}
import play.api.data.Form

trait FormMappings extends Constraints with FormKeys {
  self: WithSerialiser =>

  private final val maxTextFieldLength = 256
  private final val maxExplanationFieldLength = 500

  val nameMapping = mapping(
    keys.firstName.key -> optional(text.verifying(firstNameMaxLengthError, _.size <= maxTextFieldLength)).verifying("Please enter your first name", _.nonEmpty),
    keys.middleNames.key -> optional(nonEmptyText.verifying(middleNameMaxLengthError, _.size <= maxTextFieldLength)),
    keys.lastName.key -> optional(text.verifying(lastNameMaxLengthError, _.size <= maxTextFieldLength)).verifying("Please enter your last name", _.nonEmpty)
  ) (
    (firstName, middleName, lastName) => Name(firstName.get, middleName, lastName.get)
  ) (
    name => Some(Some(name.firstName), name.middleNames, Some(name.lastName))
  )

  val previousNameMapping = mapping(
    keys.hasPreviousName.key -> boolean,
    keys.previousName.key -> optional(nameMapping)
  ) (PreviousName.apply) (PreviousName.unapply)

  val contactMapping = mapping(
    keys.contactType.key -> text.verifying("Please select a contact method", method => List("phone", "post", "text", "email").contains(method)),
    keys.post.key -> optional(nonEmptyText.verifying(postMaxLengthError, _.size <= maxTextFieldLength)),
    keys.phone.key -> optional(nonEmptyText.verifying(postMaxLengthError, _.size <= maxTextFieldLength)),
    keys.textNum.key -> optional(nonEmptyText.verifying(postMaxLengthError, _.size <= maxTextFieldLength)),
    keys.email.key -> optional(nonEmptyText.verifying(postMaxLengthError, _.size <= maxTextFieldLength))
  ) (Contact.apply) (Contact.unapply) verifying(contactEmailConstraint, contactTelephoneConstraint, contactTextConstraint)

  val nationalityMapping = mapping(
    keys.nationalities.key -> list(nonEmptyText.verifying(nationalityMaxLengthError, _.size <= maxTextFieldLength)),
    keys.hasOtherCountry.key -> optional(boolean),
    keys.otherCountries.key -> list(text.verifying(nationalityMaxLengthError, _.size <= maxTextFieldLength)),
    keys.noNationalityReason.key -> optional(nonEmptyText.verifying(noNationalityReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (Nationality.apply) (Nationality.unapply) verifying("Please select your Nationality", nationality => {
    (nationality.nationalities.size > 0 || (nationality.otherCountries.filter(_.nonEmpty).size > 0 && nationality.hasOtherCountry.exists(b => b))) || nationality.noNationalityReason.isDefined
  }) verifying("You can specify no more than five countries", mapping => mapping.nationalities.size + mapping.otherCountries.size <=5)

  val possibleAddressMapping = mapping(
    keys.jsonList.key -> nonEmptyText
  ) (serialiser.fromJson[Addresses]) (list => Some(serialiser.toJson(list)))

  val addressMapping = mapping(
    keys.address.key -> optional(nonEmptyText.verifying(addressMaxLengthError, _.size <= maxTextFieldLength)).verifying("Please select your address", address => address.exists(_ != "Select your address")),
    keys.postcode.key -> nonEmptyText.verifying("Your postcode is not valid", postcode => PostcodeValidator.isValid(postcode))
  ) (Address.apply) (Address.unapply)

  val previousAddressMapping = mapping(
    keys.movedRecently.key -> boolean,
    keys.previousAddress.key -> optional(addressMapping)
  ) (PreviousAddress.apply) (PreviousAddress.unapply)
    .verifying("Please enter your postcode", p => (p.movedRecently && p.previousAddress.isDefined) || !p.movedRecently)
    .verifying("Please answer this question", p => p.movedRecently || (!p.movedRecently && !p.previousAddress.isDefined))

  val otherAddressMapping = mapping(
    keys.hasOtherAddress.key -> boolean
  ) (OtherAddress.apply) (OtherAddress.unapply)

  val optInMapping = single(
    keys.optIn.key -> boolean
  )

  val dobMapping = mapping(
    keys.year.key -> text.verifying("Please enter your year of birth", _.nonEmpty).verifying("The year you provided is invalid", year => year.isEmpty || year.matches("\\d+")),
    keys.month.key -> text.verifying("Please enter your month of birth", _.nonEmpty).verifying("The month you provided is invalid", month => month.isEmpty || month.matches("\\d+")),
    keys.day.key -> text.verifying("Please enter your day of birth", _.nonEmpty).verifying("The day you provided is invalid", day => day.isEmpty || day.matches("\\d+"))
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
    keys.nino.key -> optional(nonEmptyText.verifying("Your National Insurance number is not correct", nino => NinoValidator.isValid(nino))),
    keys.noNinoReason.key -> optional(nonEmptyText.verifying(noNinoReasonMaxLengthError, _.size <= maxExplanationFieldLength))
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
