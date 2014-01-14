package uk.gov.gds.ier.test

import play.api.data.Form
import play.api.test.FakeRequest
import play.api.mvc.Cookie
import org.joda.time.DateTime
import uk.gov.gds.ier.model.{InprogressOrdinary, InprogressApplication}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.security._
import play.api.mvc.Cookie

trait TestHelpers {

  val jsonSerialiser = new JsonSerialiser

  implicit class EasyGetErrorMessageError(form: ErrorTransformForm[_]) {
    def errorMessages(key:String) = form.errors(key).map(_.message)
    def globalErrorMessages = form.globalErrors.map(_.message)
    def prettyPrint = form.errors.map(error => s"${error.key} -> ${error.message}")
  }

  implicit class FakeRequestWithOurSessionCookies[A](request: FakeRequest[A]) {
    def withIerSession[T <: InprogressApplication[T]](timeSinceInteraction:Int = 1, application: Option[T] = None) = {

      val (encryptedSessionPayloadValue, payloadCookieKey) = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService)).encrypt(jsonSerialiser.toJson(application), new EncryptionKeys(new Base64EncodingService).cookies.getPublic)
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = new EncryptionService (new AesEncryptionService(new Base64EncodingService), new RsaEncryptionService(new Base64EncodingService)).encrypt(DateTime.now.minusMinutes(timeSinceInteraction).toString(), new EncryptionKeys(new Base64EncodingService).cookies.getPublic)

      val cookies =
        Seq(Cookie("sessionKey", encryptedSessionTokenValue.filter(_ >= ' '))) ++
        application.map(a => Seq(Cookie("sessionPayload", encryptedSessionPayloadValue.filter(_ >= ' ')))).getOrElse(Seq.empty)  ++
        Seq(Cookie("payloadCookieKey", payloadCookieKey)) ++
        Seq(Cookie("sessionTokenCookieKey", sessionTokenCookieKey))

      request.withCookies(cookies:_*)
    }
    def withInvalidSession() = withIerSession(6)
  }
}
