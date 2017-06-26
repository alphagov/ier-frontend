package uk.gov.gds.ier.model

case class PreviousName(hasPreviousName: Boolean,
                        hasPreviousNameOption: String,
                        changedNameBeforeLeavingUKOption: Option[String] = None,
                        previousName: Option[Name],
                        reason: Option[String] = None) {
  def toApiMap(prefix:String = "p"):Map[String,String] = {
    val previousNameMap =
      if(hasPreviousName) previousName.map(pn =>
        pn.toApiMap(prefix + "fn", prefix + "mn", prefix + "ln") ++
        reason.map(reason => Map("nameChangeReason" -> reason)).getOrElse(Map.empty)
      )
      else None

    previousNameMap.getOrElse(Map.empty)
  }
}

object PreviousName extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.hasPreviousName.key -> boolean,
    keys.hasPreviousNameOption.key -> text,
    keys.changedNameBeforeLeavingUKOption.key -> optional(text),
    keys.previousName.key -> optional(Name.mapping),
    keys.reason.key -> optional(text)
  ) (
    (hasPreviousName, hasPreviousNameOption, changedNameBeforeLeavingUKOption, name, reason) => {
      if (
          //The citizen has a previous name if they select TRUE or OTHER (crown / forces / ordinary)
          //...or if they select FALSE and then TRUE for the changedbeforeleavingUK question (overseas)
          hasPreviousNameOption.equalsIgnoreCase("true") |
          hasPreviousNameOption.equalsIgnoreCase("other") |
          changedNameBeforeLeavingUKOption.getOrElse("").equalsIgnoreCase("true")
        )
        PreviousName(
          hasPreviousName = true,
          hasPreviousNameOption = hasPreviousNameOption,
          changedNameBeforeLeavingUKOption = changedNameBeforeLeavingUKOption,
          previousName = name,
          reason = reason)
      else
        PreviousName(
          hasPreviousName = false,
          hasPreviousNameOption = hasPreviousNameOption,
          changedNameBeforeLeavingUKOption = changedNameBeforeLeavingUKOption,
          previousName = None,
          reason = None)
    }
  ) (
    PreviousName.unapply
  )
}

