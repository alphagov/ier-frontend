package uk.gov.gds.ier.model

case class PreviouslyRegistered(hasPreviouslyRegistered: Boolean) {
  def toApiMap(lastReg: Option[LastRegisteredToVote]) = {
    if (hasPreviouslyRegistered) Map("lastcategory" -> "overseas")
    else lastReg.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class LastRegisteredToVote (lastRegisteredType:LastRegisteredType) {
  def toApiMap = Map("lastcategory" -> lastRegisteredType.name)
}