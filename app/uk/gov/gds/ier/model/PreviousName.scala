package uk.gov.gds.ier.model

case class PreviousName(hasPreviousName:Boolean,
                        previousName:Option[Name]) {
  def toApiMap(prefix:String = "p"):Map[String,String] = {
    Map() ++ previousName.map(pn =>
      pn.toApiMap(prefix + "fn", prefix + "mn", prefix + "ln")
    ).getOrElse(Map.empty)
  }
}
