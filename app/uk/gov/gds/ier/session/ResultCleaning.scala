package uk.gov.gds.ier.session

import uk.gov.gds.ier.guice.{WithConfig, WithEncryption}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.mvc.{DiscardingCookie, Result}
import org.joda.time.DateTime

trait ResultCleaning extends ResultHandling {
  self: WithEncryption with WithSerialiser with WithConfig =>

  implicit class InProgressResultCleaning(result:Result) extends SessionKeys {
    def emptySession()(implicit request: play.api.mvc.Request[_]) = {
      val requestCookies = DiscardingCookie(sessionPayloadKey) ::
        DiscardingCookie(sessionTokenKey) :: Nil

      result.discardingCookies(requestCookies: _*)
    }

    def withFreshSession() = {
      val encryptedSessionTokenValue = encryptionService.encrypt(DateTime.now.toString())
      result.withCookies(
        createSecureCookie(sessionTokenKey, encryptedSessionTokenValue.filter(_ >= ' ')))
        .discardingCookies(DiscardingCookie(sessionPayloadKey))
    }
  }
}
