package uk.gov.gds.ier.session

trait SessionKeys {
  val sessionPayloadKey = "application"
  val sessionTokenKey = "sessionKey"

  val sessionPayloadKeyIV = "applicationIV"
  val sessionTokenKeyIV = "sessionKeyIV"

  val confirmationCookieKey = "confirmation"
  val confirmationCookieKeyIV = "confirmationIV"
}

object SessionKeysConstants extends SessionKeys
