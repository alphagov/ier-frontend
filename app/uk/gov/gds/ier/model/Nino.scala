package uk.gov.gds.ier.model

case class Nino(nino:Option[String],
                noNinoReason:Option[String]) {
  def toApiMap = {
    nino.map(n => Map("nino" -> n)).getOrElse(Map.empty) ++
    noNinoReason.map(nonino => Map("nonino" -> nonino)).getOrElse(Map.empty)
  }
}
