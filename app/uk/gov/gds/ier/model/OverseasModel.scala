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
                              name: Option[Stub] = None,
                              nino: Option[Nino] = None,
                              address: Option[Stub] = None) extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
      dateLeftUk = this.dateLeftUk.orElse(other.dateLeftUk),
      lastRegisteredToVote = this.lastRegisteredToVote.orElse(other.lastRegisteredToVote),
      dob = this.dob.orElse(other.dob),
      nino = this.nino.orElse(other.nino)
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
                               name: Option[Stub],
                               nino: Option[Nino],
                               address: Option[Stub]) extends CompleteApplication {
  def toApiMap = {
    Map.empty ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap).getOrElse(Map.empty) ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      firstTimeRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      lastRegisteredToVote.map(_.toApiMap).getOrElse(Map.empty) ++
      registeredAddress.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      name.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty)
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
