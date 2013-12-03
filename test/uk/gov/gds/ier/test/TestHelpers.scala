package uk.gov.gds.ier.test

import play.api.data.Form
import play.api.test.FakeRequest
import play.api.mvc.Cookie
import org.joda.time.DateTime
import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

trait TestHelpers {

  val jsonSerialiser = new JsonSerialiser

  implicit class EasyGetErrorMessageError(form: ErrorTransformForm[_]) {
    def errorMessages(key:String) = form.errors(key).map(_.message)
    def globalErrorMessages = form.globalErrors.map(_.message)
    def prettyPrint = form.errors.map(error => s"${error.key} -> ${error.message}")
  }

  implicit class FakeRequestWithOurSessionCookies[A](request: FakeRequest[A]) {
    def withIerSession(timeSinceInteraction:Int = 1, application: Option[InprogressApplication] = None) = {

      val (encryptedSessionPayloadValue, payloadCookieKey) = EncryptionService.encrypt(jsonSerialiser.toJson(application), EncryptionKeys.cookies.getPublic)
      val (encryptedSessionTokenValue, sessionTokenCookieKey) = EncryptionService.encrypt(DateTime.now.minusMinutes(timeSinceInteraction).toString(), EncryptionKeys.cookies.getPublic)

      val cookies = Seq(Cookie("sessionKey", encryptedSessionTokenValue.filter(_ >= ' '))) ++
        application.map(a => Seq(Cookie("sessionPayload", encryptedSessionPayloadValue.filter(_ >= ' ')))).getOrElse(Seq.empty)

      request.withCookies(cookies:_*)
        .withSession(("payloadCookieKey", payloadCookieKey),("sessionTokenCookieKey",sessionTokenCookieKey))

    }
    def withInvalidSession() = withIerSession(6)
  }
}
