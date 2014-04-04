package uk.gov.gds.ier.session

import play.api.mvc.Result
import org.joda.time.DateTime
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.step.InprogressApplication

trait ResultStoring extends ResultHandling {
  self: WithConfig
    with WithEncryption
    with WithSerialiser =>

  implicit class InProgressResultStoring(result:Result) extends SessionKeys {
    def storeInSession[B <: InprogressApplication[B]](application:B) = {
      val (encryptedSessionPayloadValue, encryptedSessionPayloadIVValue) =
        encryptionService.encrypt(serialiser.toJson(application))
      result.withCookies(
        createSecureCookie(sessionPayloadKey, encryptedSessionPayloadValue.filter(_ >= ' ')),
        createSecureCookie(sessionPayloadKeyIV, encryptedSessionPayloadIVValue.filter(_ >= ' ')))
    }

    def refreshSession() = {
      val (encryptedSessionTokenValue, encryptedSessionTokenIVValue) =
        encryptionService.encrypt(DateTime.now.toString())
      result.withCookies(
        createSecureCookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')),
        createSecureCookie(sessionTokenKeyIV, encryptedSessionTokenIVValue.filter(_ >= ' ')))
    }
  }
}