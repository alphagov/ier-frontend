package uk.gov.gds.ier.session

import play.api.mvc.{Cookie, DiscardingCookie}
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.step.InprogressApplication

trait CookieHandling extends SessionKeys {
  self: WithSerialiser
    with WithEncryption
    with WithConfig =>

  def payloadCookies[A <: InprogressApplication[A]](application: A) = {
    val payloadString = serialiser.toJson(application)
    val (payloadHash, payloadIV) = encryptionService.encrypt(payloadString)
    Seq(
      secureCookie(sessionPayloadKey, payloadHash),
      secureCookie(sessionPayloadKeyIV, payloadIV)
    )
  }

  def discardPayloadCookies() = Seq(
    DiscardingCookie(sessionPayloadKey),
    DiscardingCookie(sessionPayloadKeyIV)
  )

  def tokenCookies(token: SessionToken) = {
    val tokenString = serialiser.toJson(token)
    val (tokenHash, tokenIV) = encryptionService.encrypt(tokenString)
    Seq(
      secureCookie(sessionTokenKey, tokenHash),
      secureCookie(sessionTokenKeyIV, tokenIV)
    )
  }

  def discardTokenCookies() = Seq(
    DiscardingCookie(sessionTokenKey),
    DiscardingCookie(sessionTokenKeyIV)
  )

  def secureCookie ( name : String, value : String) : Cookie = {
    Cookie (name, value, None, "/", None, config.cookiesSecured, true)
  }
}
