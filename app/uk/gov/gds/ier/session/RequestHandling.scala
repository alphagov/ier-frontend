package uk.gov.gds.ier.session

import uk.gov.gds.ier.guice.WithEncryption
import uk.gov.gds.ier.serialiser.WithSerialiser

trait RequestHandling {
    self: WithEncryption with WithSerialiser =>

    private[session] implicit class InProgressRequest(request: play.api.mvc.Request[_]) extends SessionKeys {
        def getToken = for {
            cookie <- request.cookies.get(sessionTokenKey)
            sessionTokenKeyCookie <- request.cookies.get(sessionTokenCookieKeyParam)
        } yield encryptionService.decrypt(cookie.value, sessionTokenKeyCookie.value, encryptionKeys.cookies.getPrivate)

        def getApplication[T](implicit manifest: Manifest[T]): Option[T] = for {
            cookie <- request.cookies.get(sessionPayloadKey)
            payloadKeyCookie <- request.cookies.get(payloadCookieKeyParam)
        } yield serialiser.fromJson[T](encryptionService.decrypt(cookie.value, payloadKeyCookie.value, encryptionKeys.cookies.getPrivate))

        //        {
        //            request.cookies.get(sessionPayloadKey) flatMap { cookie =>
        //                val payloadKeyCookie = request.cookies.get(payloadCookieKeyParam)
        //                payloadKeyCookie map { key =>
        //                    val decryptedInfo = encryptionService.decrypt(cookie.value, key.value, encryptionKeys.cookies.getPrivate)
        //                    serialiser.fromJson[T](decryptedInfo)
        //                }
        //            }
        //        }
    }
}
