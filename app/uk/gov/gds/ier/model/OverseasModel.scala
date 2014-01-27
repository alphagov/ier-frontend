package uk.gov.gds.ier.model

case class InprogressOverseas(
  name: Option[Name] = None,
  previousName: Option[PreviousName] = None,
  previouslyRegistered: Option[PreviouslyRegistered] = None,
  dateLeftUk: Option[Stub] = None,
  firstTimeRegistered: Option[Stub] = None) extends InprogressApplication[InprogressOverseas] {

  def merge(other: InprogressOverseas) = {
    other.copy(
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered))
  }
}

case class OverseasApplication(
  name: Option[Name],
  previousName: Option[PreviousName],
  previouslyRegistered: Option[PreviouslyRegistered],
  dateLeftUk: Option[Stub],
  firstTimeRegistered: Option[Stub]) extends CompleteApplication {
  def toApiMap = {
    Map.empty ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap).getOrElse(Map.empty) ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      firstTimeRegistered.map(_.toApiMap).getOrElse(Map.empty)
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
