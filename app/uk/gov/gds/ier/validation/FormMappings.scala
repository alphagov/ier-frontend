package uk.gov.gds.ier.validation

import play.api.data.Forms._
import uk.gov.gds.ier.model._

trait FormMappings extends FormKeys {

  private final val maxTextFieldLength = 256
  private final val maxExplanationFieldLength = 500

  val nameMapping = mapping(
    firstName -> text(maxLength = maxTextFieldLength).verifying("Please enter your first name", _.nonEmpty),
    middleNames -> optional(nonEmptyText(maxLength = maxTextFieldLength)),
    lastName -> text(maxLength = maxTextFieldLength).verifying("Please enter your last name", _.nonEmpty)
  ) (Name.apply) (Name.unapply)

  val previousNameMapping = mapping(
    hasPreviousName -> boolean,
    previousName -> optional(nameMapping)
  ) (PreviousName.apply) (PreviousName.unapply)

  val contactMapping = mapping(
    contactType -> text.verifying("Please answer this question", contact => List("text", "phone", "email", "post").contains(contact)),
    post -> optional(nonEmptyText(maxLength = maxTextFieldLength)),
    phone -> optional(nonEmptyText(maxLength = maxTextFieldLength)),
    textNum -> optional(nonEmptyText(maxLength = maxTextFieldLength)),
    email -> optional(nonEmptyText(maxLength = maxTextFieldLength))
  ) (Contact.apply) (Contact.unapply)

  val nationalityMapping = mapping(
    nationalities -> list(nonEmptyText(maxLength = maxTextFieldLength)).verifying("You can specify no more than five countries", _.size <=5),
    otherCountries -> list(nonEmptyText(maxLength = maxTextFieldLength)),
    noNationalityReason -> optional(nonEmptyText(maxLength = maxExplanationFieldLength))
  ) (Nationality.apply) (Nationality.unapply) verifying("Please select your Nationality", nationality => {
    (nationality.nationalities.size > 0 || nationality.otherCountries.size > 0) || nationality.noNationalityReason.isDefined
  })

  val addressMapping = mapping(
    address -> nonEmptyText(maxLength = maxTextFieldLength).verifying("Please select your address", address => address != "Select your address"),
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
    year -> number,
    month -> number,
    day -> number
  ) {
    (year, month, day) => DateOfBirth(year, month, day)
  } {
    dateOfBirth => Some(dateOfBirth.year, dateOfBirth.month, dateOfBirth.day)
  } verifying(
    "The date you specified is invalid", dob => DateValidator.isExistingDateInThePast(dob) && !DateValidator.isTooOldToBeAlive(dob)
    ) verifying(
    "Minimum age to register to vote is %d".format(DateValidator.minimumAge), !DateValidator.isTooYoungToRegister(_)
    )

  val ninoMapping = mapping(
    nino -> optional(nonEmptyText.verifying("Your National Insurance number is not correct", nino => NinoValidator.isValid(nino))),
    noNinoReason -> optional(nonEmptyText(maxLength = maxExplanationFieldLength))
  ) (Nino.apply) (Nino.unapply)
}
