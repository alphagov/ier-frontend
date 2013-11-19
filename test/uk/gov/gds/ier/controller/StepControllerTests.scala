package uk.gov.gds.ier.controller

import org.scalatest.{Matchers, FlatSpec}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.model.{Name, InprogressApplication}
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._
import uk.gov.gds.ier.validation.{ErrorTransformer, WithErrorTransformer, InProgressForm}
import play.api.templates.Html
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.validation.InProgressForm
import play.api.mvc.Call
import uk.gov.gds.ier.validation.InProgressForm
import play.api.test.FakeApplication
import play.api.mvc.SimpleResult
import org.joda.time.DateTime
import play.api.libs.iteratee.Iteratee
import org.mockito.Matchers._
import uk.gov.gds.ier.test.TestHelpers

class StepControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  val mockErrorTransformer = mock[ErrorTransformer]

  val mockEditCall = mock[Call]
  val mockStepCall = mock[Call]

  def createController(form: Form[InprogressApplication]) = new StepController with WithSerialiser with WithErrorTransformer {
    val serialiser = jsonSerialiser
    val errorTransformer = mockErrorTransformer

    def goToNext(currentState: InprogressApplication) = Redirect("/next-step")
    override def goToConfirmation(currentState: InprogressApplication) = Redirect("/confirmation")

    val stepPostRoute: Call = mockStepCall
    val editPostRoute: Call = mockEditCall
    val validation: Form[InprogressApplication] = form
    def template(form: InProgressForm, call: Call):Html = Html("This is the template.")
  }

  behavior of "StepController.get"
  it should "return the template page for a valid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controller = createController(mock[Form[InprogressApplication]])
      val request = FakeRequest().withIerSession()

      val result = controller.get()(request)

      status(result) should be(OK)
      contentAsString(result) should be("This is the template.")
    }
  }

  it should "redirect to the start page with invalid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controller = createController(mock[Form[InprogressApplication]])
      val request = FakeRequest().withIerSession(6)

      val result = controller.get()(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/"))
    }
  }

  behavior of "StepController.post"
  it should "bind to the validation and redisplay the template on error" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = Form(
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
      val form = Form(
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
      val controller = createController(mock[Form[InprogressApplication]])
      val request = FakeRequest().withIerSession()

      val result = controller.editGet()(request)

      status(result) should be(OK)
      contentAsString(result) should be("This is the template.")
    }
  }

  it should "redirect to the start page with invalid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controller = createController(mock[Form[InprogressApplication]])
      val request = FakeRequest().withIerSession(6)

      val result = controller.editGet()(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/"))
    }
  }

  behavior of "StepController.editPost"
  it should "bind to the validation and redisplay the template on error" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = Form(
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
      val form = Form(
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
}
