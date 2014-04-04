package uk.gov.gds.ier.model

case class CitizenDetails(
    dateBecameCitizen: DOB,
    howBecameCitizen: String) {

  def toApiMap = {
    dateBecameCitizen.toApiMap("dbritcrit") ++
      Map("hbritcit" -> howBecameCitizen)
  }
}
