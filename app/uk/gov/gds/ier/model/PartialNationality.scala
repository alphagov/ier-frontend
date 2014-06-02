package uk.gov.gds.ier.model

import uk.gov.gds.ier.validation._

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

object PartialNationality extends ModelMapping with ErrorMessages {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.british.key -> optional(boolean),
    keys.irish.key -> optional(boolean),
    keys.hasOtherCountry.key -> optional(boolean),
    keys.otherCountries.key -> list(text
      .verifying(nationalityMaxLengthError, _.size <= maxTextFieldLength)),
    keys.noNationalityReason.key -> optional(nonEmptyText
      .verifying(noNationalityReasonMaxLengthError, _.size <= maxExplanationFieldLength))
  ) (
    PartialNationality.apply
  ) (
    PartialNationality.unapply
  )
}
