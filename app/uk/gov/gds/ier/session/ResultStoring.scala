package uk.gov.gds.ier.session

import play.api.mvc.Result
import uk.gov.gds.ier.model.InprogressApplication
import org.joda.time.DateTime
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.WithSerialiser

trait ResultStoring extends ResultHandling {
  self: WithConfig
    with WithEncryption
    with WithSerialiser =>

  implicit class InProgressResultStoring(result:Result) extends SessionKeys {
    def storeInSession[B <: InprogressApplication[B]](application:B) = {
      val (encryptedSessionPayloadValue, payloadCookieKey) = encryptionService.encrypt(serialiser.toJson(application), encryptionKeys.cookies.getPublic)
      result.withCookies(
        createSecureCookie(sessionPayloadKey, encryptedSessionPayloadValue.filter(_ >= ' ')),
        createSecureCookie(payloadCookieKeyParam, payloadCookieKey.filter(_ >= ' ')))
    }

    def refreshSession() = {
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = encryptionService.encrypt(DateTime.now.toString(), encryptionKeys.cookies.getPublic)
      result.withCookies(
        createSecureCookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')),
        createSecureCookie(sessionTokenCookieKeyParam, sessionTokenCookieKey.filter(_ >= ' ')))
    }
  }
}