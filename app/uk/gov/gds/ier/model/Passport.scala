package uk.gov.gds.ier.model

case class Passport(
    hasPassport: Boolean,
    bornInsideUk: Option[Boolean],
    details: Option[PassportDetails],
    citizen: Option[CitizenDetails]) {

  def toApiMap = {
    Map("bpass" -> hasPassport.toString) ++
      details.map(_.toApiMap).getOrElse(Map.empty) ++
      citizen.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class PassportDetails(
    passportNumber: String,
    authority: String,
    issueDate: DOB) {

  def toApiMap = {
    Map(
      "passno" -> passportNumber,
      "passloc" -> authority
    ) ++ issueDate.toApiMap("passdate")
  }
}
