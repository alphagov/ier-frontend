package uk.gov.gds.ier.model

import play.api.data.Forms._

trait FormMappings extends FormKeys {

  val nameMapping = mapping(
    firstName -> nonEmptyText,
    middleNames -> optional(nonEmptyText),
    lastName -> nonEmptyText
  ) (Name.apply) (Name.unapply)

  val contactMapping = mapping(
    contactType -> nonEmptyText,
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
    address -> nonEmptyText,
    postcode -> nonEmptyText
  ) (Address.apply) (Address.unapply)

  val dobMapping = mapping(
    year -> nonEmptyText,
    month -> nonEmptyText,
    day -> nonEmptyText
  ) (DateOfBirth.apply) (DateOfBirth.unapply)

}
