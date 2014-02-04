package uk.gov.gds.ier.model

import uk.gov.gds.ier.model.LastRegisteredType.LastRegisteredType

case class InprogressOverseas(name: Option[Name] = None,
                              previousName: Option[PreviousName] = None, 
                              previouslyRegistered: Option[PreviouslyRegistered] = None,
                              dateLeftUk: Option[DateLeftUk] = None,
                              firstTimeRegistered: Option[Stub] = None,
                              lastRegisteredToVote: Option[LastRegisteredToVote] = None,
                              registeredAddress: Option[Stub] = None,
							  dob: Option[DateOfBirth] = None,
                              nino: Option[Nino] = None,
                              address: Option[OverseasAddress] = None,
                              openRegisterOptin: Option[Boolean] = None,
                              waysToVote: Option[Stub] = None,
                              postalVote: Option[Stub] = None,
                              contact: Option[Contact] = None) extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
      dateLeftUk = this.dateLeftUk.orElse(other.dateLeftUk),
      firstTimeRegistered = this.firstTimeRegistered.orElse(other.firstTimeRegistered),
      lastRegisteredToVote = this.lastRegisteredToVote.orElse(other.lastRegisteredToVote),
      registeredAddress = this.registeredAddress.orElse(other.registeredAddress),
      dob = this.dob.orElse(other.dob),
      nino = this.nino.orElse(other.nino),
      address = this.address.orElse(other.address),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      waysToVote = this.waysToVote.orElse(other.waysToVote),
      contact = this.contact.orElse(other.contact)
    )
  }
}

case class OverseasApplication(
                               name: Option[Name],
                               previousName: Option[PreviousName],
                               previouslyRegistered: Option[PreviouslyRegistered],
                               dateLeftUk: Option[DateLeftUk],
                               firstTimeRegistered: Option[Stub],
                               lastRegisteredToVote: Option[LastRegisteredToVote],
                               registeredAddress: Option[Stub],
                               dob: Option[DateOfBirth],
                               nino: Option[Nino],
                               address: Option[OverseasAddress],
                               openRegisterOptin: Option[Boolean],
                               waysToVote: Option[Stub],
                               postalVote: Option[Stub],
                               contact: Option[Contact]) extends CompleteApplication {
  def toApiMap = {
    Map.empty ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap).getOrElse(Map.empty) ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty) ++
      firstTimeRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      lastRegisteredToVote.map(_.toApiMap).getOrElse(Map.empty) ++
      registeredAddress.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class Stub() {
  def toApiMap = Map.empty
}

case class PreviouslyRegistered(hasPreviouslyRegistered: Boolean) {
  def toApiMap = {
    if (hasPreviouslyRegistered) Map("povseas" -> "true")
    else Map("povseas" -> "false")
  }
}


case class DateLeftUk (year:Int, month:Int) {
  def toApiMap = {
    Map("dlu" -> "%04d-%02d".format(year,month))
  }
}

case class LastRegisteredToVote (lastRegisteredType:LastRegisteredType) {
  def toApiMap = Map.empty
}

object LastRegisteredType extends Enumeration {
  type LastRegisteredType = Value
  val UK = Value("uk")
  val Army = Value("army")
  val Crown = Value("crown")
  val Council = Value("council")
  val NotRegistered = Value("not-registered")
}

case class OverseasAddress(country: Option[String], addressDetails: Option[String]) {
    def toApiMap = Map("country" -> country.getOrElse(""), "addressDetails" -> addressDetails.getOrElse(""))
}
case class CountryWithCode(country: String, code: String) 
