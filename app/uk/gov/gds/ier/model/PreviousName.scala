package uk.gov.gds.ier.model

case class PreviousName(hasPreviousName:Boolean,
                        previousName:Option[Name]) {
  def toApiMap(prefix:String = "p"):Map[String,String] = {
    val previousNameMap =
      if(hasPreviousName) previousName.map(pn => pn.toApiMap(prefix + "fn", prefix + "mn", prefix + "ln"))
      else None

    previousNameMap.getOrElse(Map.empty)
  }
}

object PreviousName extends ModelMapping {
  import playMappings._

  val mapping = playMappings.mapping(
    keys.hasPreviousName.key -> boolean,
    keys.previousName.key -> optional(Name.mapping)
  ) (
    (hasPreviousName, name) => PreviousName(
      hasPreviousName = hasPreviousName,
      previousName = hasPreviousName match {
        case false => None
        case _ => name
      }
    )
  ) (
    PreviousName.unapply
  )
}

