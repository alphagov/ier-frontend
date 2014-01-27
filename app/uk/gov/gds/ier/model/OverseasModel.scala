package uk.gov.gds.ier.model

case class InprogressOverseas(previouslyRegistered: Option[PreviouslyRegistered] = None,
                              dateLeftUk: Option[Stub] = None,
                              dob: Option[DOB] = None,
                              firstTimeRegistered: Option[Stub] = None) extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
      dob = this.dob.orElse(other.dob)
    )
  }
}

case class OverseasApplication(previouslyRegistered: Option[PreviouslyRegistered],
                               dateLeftUk: Option[Stub],
                               dob: Option[DOB],
                               firstTimeRegistered: Option[Stub]) extends CompleteApplication {
  def toApiMap = {
    Map.empty ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
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
