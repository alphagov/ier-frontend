package uk.gov.gds.ier.localAuthority

import uk.gov.gds.ier.model.LocalAuthority

case class LocalAuthorityRequest(
  sourcePath: Option[String],
  postcode: String
)



object LocalAuthorityRequest {
  def apply(): LocalAuthorityRequest = {
    LocalAuthorityRequest(None, "")
  }
}
