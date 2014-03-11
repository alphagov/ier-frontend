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
      val encryptedSessionPayloadValue = encryptionService.encrypt(serialiser.toJson(application))
      result.withCookies(
        createSecureCookie(sessionPayloadKey, encryptedSessionPayloadValue.filter(_ >= ' ')))
    }

    def refreshSession() = {
      val encryptedSessionTokenValue = encryptionService.encrypt(DateTime.now.toString())
      result.withCookies(
        createSecureCookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')))
    }
  }
}