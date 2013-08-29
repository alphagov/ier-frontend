package uk.gov.gds.ier.model

class ApiResponse(body: String)

case class Success(body: String) extends ApiResponse(body)
case class Fail(body: String) extends ApiResponse(body)
