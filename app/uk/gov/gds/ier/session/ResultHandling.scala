package uk.gov.gds.ier.session

import play.api.mvc.Cookie
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.step.InprogressApplication
import play.api.mvc.{DiscardingCookie, Result, Request}

trait ResultHandling extends CookieHandling {
  self: WithConfig
    with WithEncryption
    with WithSerialiser =>

  implicit class ResultWithCookieOps(result:Result) extends SessionKeys {

    def storeInSession[B <: InprogressApplication[B]](
      application:B
    ) (
      implicit request: Request[_]
    ) = {
      val domain = getDomain(request)
      result.withCookies(payloadCookies(application, domain):_*)
    }

    def storeToken(
      token: SessionToken
    ) (
      implicit request: Request[_]
    ) = {
      val domain = getDomain(request)
      result.withCookies(tokenCookies(token, domain):_*)
    }

    def emptySession()(implicit request: Request[_]) = {
      val domain = getDomain(request)
      result.discardingCookies(
        discardPayloadCookies(domain) ++ discardTokenCookies(domain):_*
      )
    }

    def withFreshSession()(implicit request: Request[_]) = {
      val domain = getDomain(request)
      val resultWithToken = result storeToken SessionToken()
      resultWithToken.discardingCookies(discardPayloadCookies(domain):_*)
    }
  }

  def getDomain(request: Request[_]) = {
    request.headers.get("host") filterNot {
      _ startsWith "localhost"
    } filterNot {
      _ == ""
    } map {
      _.split(":").head
    }
  }
}
