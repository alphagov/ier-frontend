package uk.gov.gds.ier.session

import uk.gov.gds.ier.guice.WithEncryption
import uk.gov.gds.ier.serialiser.WithSerialiser

trait RequestHandling {
  self: WithEncryption with WithSerialiser =>

  private[session] implicit class InProgressRequest(request:play.api.mvc.Request[_]) extends SessionKeys {
    def getToken = {
      val cookie = request.cookies.get(sessionTokenKey)
      if (cookie.isDefined) {
        val sessionTokenKeyCookie = request.cookies.get(sessionTokenCookieKeyParam)
        if (sessionTokenKeyCookie.isDefined) {
          val decryptedInfo = encryptionService.decrypt(cookie.get.value, sessionTokenKeyCookie.get.value ,encryptionKeys.cookies.getPrivate)
          Some(decryptedInfo)
        }
        else None
      }
      else None
    }

    def getApplication[T](implicit manifest:Manifest[T]) : Option[T] = {
      request.cookies.get(sessionPayloadKey) flatMap { cookie =>
        val payloadKeyCookie = request.cookies.get(payloadCookieKeyParam)
        payloadKeyCookie map { key =>
          val decryptedInfo = encryptionService.decrypt(cookie.value, key.value, encryptionKeys.cookies.getPrivate)
          serialiser.fromJson[T](decryptedInfo)
        }
      }
    }
  }
}
