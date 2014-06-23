package uk.gov.gds.ier.session

trait SessionKeys {
  val sessionPayloadKey = "application"
  val sessionTokenKey = "sessionKey"

  val sessionPayloadKeyIV = "applicationIV"
  val sessionTokenKeyIV = "sessionKeyIV"

  /**
   * Confirmation cookie replaces 'application' cookie for Confirmation to Complete transition.
   * The counterpart initialization vector cookie is still 'applicationIV'
   */
  val sessionCompleteStepKey = "confirmation"
}
