package uk.gov.gds.ier.test

import play.api.data.Form
import play.api.test.FakeRequest
import play.api.mvc.Cookie
import org.joda.time.DateTime
import uk.gov.gds.ier.model.InprogressApplication
import uk.gov.gds.ier.serialiser.JsonSerialiser

trait TestHelpers {

  val jsonSerialiser = new JsonSerialiser

  implicit class EasyGetErrorMessageError(form: Form[_]) {
    def errorMessages(key:String) = form.errors(key).map(_.message)
    def prettyPrint = form.errors.map(error => s"${error.key} -> ${error.message}")
  }

  implicit class FakeRequestWithOurSessionCookies[A](request: FakeRequest[A]) {
    def withIerSession(timeSinceInteraction:Int = 1, application: Option[InprogressApplication] = None) = {
      val cookies = Seq(Cookie("sessionKey", DateTime.now.minusMinutes(timeSinceInteraction).toString())) ++
        application.map(a => Seq(Cookie("sessionPayload", jsonSerialiser.toJson(a)))).getOrElse(Seq.empty)

      request.withCookies(cookies:_*)
    }
    def withInvalidSession() = withIerSession(6)
  }
}
