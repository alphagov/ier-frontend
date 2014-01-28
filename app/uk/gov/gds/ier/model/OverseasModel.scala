package uk.gov.gds.ier.model

case class InprogressOverseas(previouslyRegistered: Option[PreviouslyRegistered] = None,
                              dateLeftUk: Option[Stub] = None,
                              nino: Option[Stub] = None,
                              address: Option[OverseasAddress] = None,
                              openRegister: Option[Stub] = None,
                              firstTimeRegistered: Option[Stub] = None) extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
            address = this.address.orElse(other.address))
  }
}

case class OverseasApplication(previouslyRegistered: Option[PreviouslyRegistered],
                               dateLeftUk: Option[Stub],
                               nino: Option[Stub],
                               address: Option[OverseasAddress],
                               openRegister: Option[Stub],
                               firstTimeRegistered: Option[Stub]) extends CompleteApplication {
  def toApiMap = {
    Map.empty ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty) ++
      openRegister.map(_.toApiMap).getOrElse(Map.empty) ++
      firstTimeRegistered.map(_.toApiMap).getOrElse(Map.empty)
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

case class OverseasAddress(country: String, addressDetails: String) {
    def toApiMap = Map("country" -> country, "addressDetails" -> addressDetails)
}