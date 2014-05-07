package uk.gov.gds.ier.session

import play.api.mvc.Cookie
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.step.InprogressApplication
import play.api.mvc.{DiscardingCookie, Result}

trait ResultHandling extends CookieHandling {
  self: WithConfig
    with WithEncryption
    with WithSerialiser =>

  implicit class ResultWithCookieOps(result:Result) extends SessionKeys {

    def storeInSession[B <: InprogressApplication[B]](application:B) = {
      result.withCookies(payloadCookies(application):_*)
    }

    def storeToken(token: SessionToken) = {
      result.withCookies(tokenCookies(token):_*)
    }

    def emptySession()(implicit request: play.api.mvc.Request[_]) = {
      result.discardingCookies(discardPayloadCookies ++ discardTokenCookies:_*)
    }

    def withFreshSession() = {
      val resultWithToken = result storeToken SessionToken()
      resultWithToken.discardingCookies(discardPayloadCookies:_*)
    }
  }

  def createSecureCookie ( name : String, value : String) : Cookie = {
    Cookie (name, value, None, "/", None, config.cookiesSecured, true)
  }
}
