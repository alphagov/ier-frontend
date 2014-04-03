package uk.gov.gds.ier.model

case class Job(
    jobTitle: Option[String],
    govDepartment: Option[String]) {

  def toApiMap =
    jobTitle.map(jobTitle => Map("role" -> jobTitle.toString)).getOrElse(Map.empty) ++
    govDepartment.map(govDepartment => Map("dept" -> govDepartment.toString)).getOrElse(Map.empty)
}