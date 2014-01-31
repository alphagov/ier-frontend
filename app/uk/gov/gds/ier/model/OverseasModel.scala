package uk.gov.gds.ier.model

import uk.gov.gds.ier.model.LastRegisteredType.LastRegisteredType

case class InprogressOverseas(previouslyRegistered: Option[PreviouslyRegistered] = None,
                              dateLeftUk: Option[DateLeftUk] = None,
                              firstTimeRegistered: Option[Stub] = None,
                              lastRegisteredToVote: Option[LastRegisteredToVote] = None,
                              registeredAddress: Option[Stub] = None,
                              dob: Option[DateOfBirth] = None,
                              address: Option[Stub] = None,
                              openRegisterOptin: Option[Boolean] = None,
                              waysToVote: Option[Stub] = None) extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
      dateLeftUk = this.dateLeftUk.orElse(other.dateLeftUk),
      firstTimeRegistered = this.firstTimeRegistered.orElse(other.firstTimeRegistered),
      lastRegisteredToVote = this.lastRegisteredToVote.orElse(other.lastRegisteredToVote),
      registeredAddress = this.registeredAddress.orElse(other.registeredAddress),
      dob = this.dob.orElse(other.dob),
      address = this.address.orElse(other.address),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      waysToVote = this.waysToVote.orElse(other.waysToVote)
    )
  }
}

case class OverseasApplication(previouslyRegistered: Option[PreviouslyRegistered],
                               dateLeftUk: Option[DateLeftUk],
                               firstTimeRegistered: Option[Stub],
                               lastRegisteredToVote: Option[LastRegisteredToVote],
                               registeredAddress: Option[Stub],
                               dob: Option[DateOfBirth],
                               address: Option[Stub],
                               openRegisterOptin: Option[Boolean],
                               waysToVote: Option[Stub]) extends CompleteApplication {
  def toApiMap = {
    Map.empty ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      firstTimeRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      lastRegisteredToVote.map(_.toApiMap).getOrElse(Map.empty) ++
      registeredAddress.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty)
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
