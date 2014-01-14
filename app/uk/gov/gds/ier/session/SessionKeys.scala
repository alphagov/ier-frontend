package uk.gov.gds.ier.session

trait SessionKeys {
  val sessionPayloadKey = "application"
  val sessionTokenKey = "sessionKey"

  val sessionTokenCookieKeyParam = "sessionTokenCookieKey"
  val payloadCookieKeyParam = "payloadCookieKey"
}
