package uk.gov.gds.ier.session

import uk.gov.gds.ier.guice.WithEncryption
import uk.gov.gds.ier.serialiser.WithSerialiser
import scala.util.Try

trait RequestHandling {
    self: WithEncryption with WithSerialiser =>

    private[session] implicit class InProgressRequest(request: play.api.mvc.Request[_])
      extends SessionKeys {
        def getToken: Option[SessionToken] = {
          val sessionToken = for {
            cookie <- request.cookies.get(sessionTokenKey)
            cookieInitVec <- request.cookies.get(sessionTokenKeyIV)
          } yield Try {
            val json = encryptionService.decrypt(cookie.value, cookieInitVec.value)
            serialiser.fromJson[SessionToken](json)
          }
          sessionToken.flatMap(_.toOption)
        }

        def getApplication[T](implicit manifest: Manifest[T]): Option[T] = {
          val application = for {
            cookie <- request.cookies.get(sessionPayloadKey)
            cookieInitVec <- request.cookies.get(sessionPayloadKeyIV)
          } yield Try {
            val json = encryptionService.decrypt(cookie.value,  cookieInitVec.value)
            serialiser.fromJson[T](json)
          }
          application.flatMap(_.toOption)
        }
    }
}
