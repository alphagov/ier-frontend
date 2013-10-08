package uk.gov.gds.ier.validation

import play.api.data.Forms._
import uk.gov.gds.ier.model._

trait FormMappings extends FormKeys {

  val nameMapping = mapping(
    firstName -> text.verifying("Please enter your first name", _.nonEmpty),
    middleNames -> optional(nonEmptyText),
    lastName -> text.verifying("Please enter your last name", _.nonEmpty)
  ) (Name.apply) (Name.unapply)

  val previousNameMapping = mapping(
    hasPreviousName -> boolean,
    previousName -> optional(nameMapping)
  ) (PreviousName.apply) (PreviousName.unapply)

  val contactMapping = mapping(
    contactType -> text.verifying("Please answer this question", contact => List("text", "phone", "email", "post").contains(contact)),
    post -> optional(nonEmptyText),
    phone -> optional(nonEmptyText),
    textNum -> optional(nonEmptyText),
    email -> optional(nonEmptyText)
  ) (Contact.apply) (Contact.unapply)

  val nationalityMapping = mapping(
    nationalities -> list(nonEmptyText),
    otherCountries -> list(nonEmptyText),
    noNationalityReason -> optional(nonEmptyText)
  ) (Nationality.apply) (Nationality.unapply) verifying("Please select your Nationality", nationality => {
    (nationality.nationalities.size > 0 || nationality.otherCountries.size > 0) || nationality.noNationalityReason.isDefined
  })

  val addressMapping = mapping(
    address -> nonEmptyText.verifying("Please select your address", address => address != "Select your address"),
    postcode -> nonEmptyText
  ) (Address.apply) (Address.unapply)

  val previousAddressMapping = mapping(
    movedRecently -> boolean,
    previousAddress -> optional(addressMapping)
  ) (PreviousAddress.apply) (PreviousAddress.unapply)

  val otherAddressMapping = mapping(
    hasOtherAddress -> boolean
  ) (OtherAddress.apply) (OtherAddress.unapply)

  val openRegisterMapping = single(
    openRegisterOptin -> boolean
  )

  val dobMapping = mapping(
    year -> nonEmptyText,
    month -> nonEmptyText,
    day -> nonEmptyText
  ) (DateOfBirth.apply) (DateOfBirth.unapply)

  val ninoMapping = mapping(
    nino -> optional(nonEmptyText.verifying("Your National Insurance number is not correct", nino => NinoValidator.isValid(nino))),
    noNinoReason -> optional(nonEmptyText)
  ) (Nino.apply) (Nino.unapply)
}
