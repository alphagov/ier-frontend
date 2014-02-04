package uk.gov.gds.ier.model

import uk.gov.gds.ier.model.LastRegisteredType.LastRegisteredType

case class InprogressOverseas(
    previouslyRegistered: Option[PreviouslyRegistered] = None,
    dateLeftUk: Option[DateLeftUk] = None,
    firstTimeRegistered: Option[Stub] = None,
    lastRegisteredToVote: Option[LastRegisteredToVote] = None,
    dob: Option[DateOfBirth] = None,
    name: Option[Stub] = None,
    nino: Option[Nino] = None,
    lastUkAddress: Option[PartialAddress] = None,
    address: Option[Stub] = None,
    possibleAddresses: Option[PossibleAddress] = None)
  extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
      dateLeftUk = this.dateLeftUk.orElse(other.dateLeftUk),
      firstTimeRegistered = this.firstTimeRegistered.orElse(other.firstTimeRegistered),
      lastRegisteredToVote = this.lastRegisteredToVote.orElse(other.lastRegisteredToVote),
      dob = this.dob.orElse(other.dob),
      name = this.name.orElse(other.name),
      nino = this.nino.orElse(other.nino),
      lastUkAddress = this.lastUkAddress.orElse(other.lastUkAddress),
      address = this.address.orElse(other.address),
      possibleAddresses = None
    )
  }
}

case class OverseasApplication(
    previouslyRegistered: Option[PreviouslyRegistered],
    dateLeftUk: Option[DateLeftUk],
    firstTimeRegistered: Option[Stub],
    lastRegisteredToVote: Option[LastRegisteredToVote],
    dob: Option[DateOfBirth],
    name: Option[Stub],
    nino: Option[Nino],
    address: Option[Stub],
    lastUkAddress: Option[PartialAddress] = None)
  extends CompleteApplication {

  def toApiMap = {
    Map.empty ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      firstTimeRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      lastRegisteredToVote.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      name.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class Stub() {
  def toApiMap = Map.empty
}

case class PreviouslyRegistered(hasPreviouslyRegistered:Boolean) {
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
