package uk.gov.gds.ier.model

case class InprogressOrdinary (
    name: Option[Name] = None,
    previousName: Option[PreviousName] = None,
    dob: Option[DateOfBirth] = None,
    nationality: Option[PartialNationality] = None,
    nino: Option[Nino] = None,
    address: Option[PartialAddress] = None,
    previousAddress: Option[PartialPreviousAddress] = None,
    otherAddress: Option[OtherAddress] = None,
    openRegisterOptin: Option[Boolean] = None,
    postalVote: Option[PostalVote] = None,
    contact: Option[Contact] = None,
    possibleAddresses: Option[PossibleAddress] = None,
    country: Option[Country] = None) extends InprogressApplication[InprogressOrdinary] {

  def merge(other: InprogressOrdinary):InprogressOrdinary = {
    other.copy(
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      dob = this.dob.orElse(other.dob),
      nationality = this.nationality.orElse(other.nationality),
      nino = this.nino.orElse(other.nino),
      address = this.address.orElse(other.address),
      previousAddress = this.previousAddress.orElse(other.previousAddress),
      otherAddress = this.otherAddress.orElse(other.otherAddress),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      postalVote = this.postalVote.orElse(other.postalVote),
      contact = this.contact.orElse(other.contact),
      possibleAddresses = None,
      country = this.country.orElse(other.country)
    )
  }
}
