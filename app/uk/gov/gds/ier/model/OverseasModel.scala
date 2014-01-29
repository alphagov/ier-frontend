package uk.gov.gds.ier.model

case class InprogressOverseas(
    previouslyRegistered: Option[PreviouslyRegistered] = None,
    dateLeftUk: Option[Stub] = None,
    firstTimeRegistered: Option[Stub] = None,
    name: Option[Stub] = None,
    lastUkAddress: Option[PartialAddress] = None,
    possibleAddresses: Option[PossibleAddress] = None)
  extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
      name = this.name.orElse(other.name),
      lastUkAddress = this.lastUkAddress.orElse(other.lastUkAddress),
      possibleAddresses = None
    )
  }
}

case class OverseasApplication(
    previouslyRegistered: Option[PreviouslyRegistered],
    dateLeftUk: Option[Stub],
    firstTimeRegistered: Option[Stub],
    name: Option[Stub],
    lastUkAddress: Option[PartialAddress] = None)
  extends CompleteApplication {

  def toApiMap = {
    Map.empty ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      firstTimeRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      name.map(_.toApiMap).getOrElse(Map.empty)
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
