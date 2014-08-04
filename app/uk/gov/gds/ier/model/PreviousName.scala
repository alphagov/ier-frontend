package uk.gov.gds.ier.model

case class PreviousName(hasPreviousName: Boolean,
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
    keys.previousName.key -> optional(Name.mapping),
    keys.reason.key -> optional(text)
  ) (
    (hasPreviousName, name, reason) => {
      if (hasPreviousName)
        PreviousName(
          hasPreviousName = true,
          previousName = name,
          reason = reason)
      else
        PreviousName(
          hasPreviousName = false,
          previousName = None,
          reason = None)
    }
  ) (
    PreviousName.unapply
  )
}

