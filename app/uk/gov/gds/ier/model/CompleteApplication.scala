package uk.gov.gds.ier.model

trait CompleteApplication {
  def toApiMap:Map[String, String]
}
