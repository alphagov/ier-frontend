package uk.gov.gds.ier.model

case class PartialNationality(
    british:Option[Boolean] = None,
    irish:Option[Boolean] = None,
    hasOtherCountry:Option[Boolean] = None,
    otherCountries:List[String] = List.empty,
    noNationalityReason:Option[String] = None) {

  def checkedNationalities = {
    british.toList.filter(_ == true).map(brit => "British") ++
    irish.toList.filter(_ == true).map(isIrish => "Irish")
  }
  def isoCheckedNationalities = {
    british.toList.filter(_ == true).map(brit => "United Kingdom") ++
    irish.toList.filter(_ == true).map(isIrish => "Ireland")
  }
}
