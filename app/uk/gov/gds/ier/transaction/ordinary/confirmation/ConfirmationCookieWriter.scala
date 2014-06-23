package uk.gov.gds.ier.transaction.ordinary.confirmation

import play.api.mvc.{Request, Result}
import uk.gov.gds.ier.session.{ResultHandling, SessionKeys}

trait ConfirmationCookieWriter {
  self: ResultHandling =>

  implicit class CookieWritertorForCompletePage(result: Result) extends SessionKeys {
    def confirmationCookieInSession[B <: AnyRef](
        payload: B)(
        implicit request: Request[_]) = {
      val domain = getDomain(request)
      result.withCookies(customPayloadCookies(payload, sessionCompleteStepKey, domain): _*)
    }
  }
}
