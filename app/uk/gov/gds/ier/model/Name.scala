package uk.gov.gds.ier.model

import uk.gov.gds.ier.validation.constants.NameConstants

case class Name(firstName:String,
                middleNames:Option[String],
                lastName:String) {
  def toApiMap(fnKey:String, mnKey:String, lnKey:String):Map[String,String] = {

    val firstNameTrunc =
      if (firstName.size > NameConstants.firstNameMaxLength)
        firstName.substring(0,NameConstants.firstNameMaxLength)
      else firstName

    val lastNameTrunc =
      if (lastName.size > NameConstants.lastNameMaxLength)
        lastName.substring(0,NameConstants.lastNameMaxLength)
      else lastName

    val middleNamesTrunc = middleNames.map(mn =>
      if (mn.size > NameConstants.middleNamesMaxLength)
        mn.substring(0,NameConstants.middleNamesMaxLength)
      else mn
    ).getOrElse("")

    Map(
      fnKey -> firstNameTrunc,
      lnKey -> lastNameTrunc
    ) ++
    middleNames.map(mn => Map(
      mnKey -> middleNamesTrunc
    )).getOrElse(Map.empty)
  }
}

object Name extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.firstName.key -> default(text, ""),
    keys.middleNames.key -> optional(text),
    keys.lastName.key -> default(text, "")
  ) (
    Name.apply
  ) (
    Name.unapply
  )
}
