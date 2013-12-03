package uk.gov.gds.ier.controller

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.model.{Address, PossibleAddress, Name, InprogressApplication}
import play.api.data.Form
import play.api.data.Forms._
import play.api.templates.Html
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Call
import uk.gov.gds.ier.validation.{ErrorTransformForm, InProgressForm}
import play.api.test.FakeApplication
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class StepControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  val mockEditCall = mock[Call]
  val mockStepCall = mock[Call]

  def createController(form: ErrorTransformForm[InprogressApplication]) = new StepController
                                                                       with WithSerialiser {

    val serialiser = jsonSerialiser

    def goToNext(currentState: InprogressApplication) = Redirect("/next-step")
    override def goToConfirmation(currentState: InprogressApplication) = Redirect("/confirmation")

    val stepPostRoute: Call = mockStepCall
    val editPostRoute: Call = mockEditCall
    val validation = form
    def template(form: InProgressForm, call: Call):Html = Html("This is the template.")
  }

  behavior of "StepController.get"
  it should "return the template page for a valid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controller = createController(mock[ErrorTransformForm[InprogressApplication]])
      val request = FakeRequest().withIerSession()

      val result = controller.get()(request)

      status(result) should be(OK)
      contentAsString(result) should be("This is the template.")
    }
  }

  it should "redirect to the start page with invalid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controller = createController(mock[ErrorTransformForm[InprogressApplication]])
      val request = FakeRequest().withIerSession(6)

      val result = controller.get()(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/"))
    }
  }

  behavior of "StepController.post"
  it should "bind to the validation and redisplay the template on error" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = ErrorTransformForm(
        mapping("foo" -> text.verifying("Forcing a failure", foo => false))
          (foo => InprogressApplication())
          (app => Some("foo")))
      val controller = createController(form)

      val request = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "some text")

      val result = controller.post()(request)

      status(result) should be(OK)
      contentAsString(result) should be("This is the template.")
    }
  }

  it should "bind to the validation and redirect to the next page on successful validation" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = ErrorTransformForm(
        mapping("foo" -> text.verifying("I will always pass", foo => true))
          (foo => InprogressApplication())
          (app => Some("foo"))
      )
      val controller = createController(form)

      val request = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "some text")

      val result = controller.post()(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/next-step"))
    }
  }

  behavior of "StepController.editGet"
  it should "return the template page for a valid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controller = createController(mock[ErrorTransformForm[InprogressApplication]])
      val request = FakeRequest().withIerSession()

      val result = controller.editGet()(request)

      status(result) should be(OK)
      contentAsString(result) should be("This is the template.")
    }
  }

  it should "redirect to the start page with invalid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controller = createController(mock[ErrorTransformForm[InprogressApplication]])
      val request = FakeRequest().withIerSession(6)

      val result = controller.editGet()(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/"))
    }
  }

  behavior of "StepController.editPost"
  it should "bind to the validation and redisplay the template on error" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = ErrorTransformForm(
        mapping("foo" -> text.verifying("Forcing a failure", foo => false))
          (foo => InprogressApplication())
          (app => Some("foo"))
      )
      val request = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "some text")

      val result = createController(form).editPost()(request)

      status(result) should be(OK)
      contentAsString(result) should be("This is the template.")
    }
  }

  it should "bind to the validation and redirect to the next page on successful validation" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = ErrorTransformForm(
        mapping("foo" -> text.verifying("I will always pass", foo => true))
          (foo => InprogressApplication())
          (app => Some("foo"))
      )
      val request = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "some text")

      val result = createController(form).editPost()(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/confirmation"))
    }
  }

  it should "not allow possibleAddresses in to the session, we don't want to store those, ever!" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = ErrorTransformForm(
        mapping("foo" -> text.verifying("I will always pass", foo => true))
          (foo => InprogressApplication(
            possibleAddresses = Some(PossibleAddress(
              addresses = List(Address(Some("123 Fake Street"), "SW1A 1AA")),
              postcode = "SW1A 1AA")),
            name = Some(Name("John", None, "Smith"))))
          (app => Some("foo"))
      )
      val request = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "some text")

      val result =  createController(form).editPost()(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/confirmation"))

      cookies(result).get("application") match {
        case Some(cookie) => {
          val cookieKey = session(result).get("payloadCookieKey").getOrElse("")
          val decryptedInfo = EncryptionService.decrypt(cookie.value, cookieKey ,EncryptionKeys.cookies.getPrivate)
          val application = jsonSerialiser.fromJson[InprogressApplication](decryptedInfo)
          application.possibleAddresses should be(None)
        }
        case _ => fail("Should have been able to deserialise")
      }
    }
  }
}
