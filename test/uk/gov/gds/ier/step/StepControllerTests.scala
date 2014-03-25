package uk.gov.gds.ier.step

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import uk.gov.gds.ier.model._
import play.api.data.Forms._
import play.api.templates.Html
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.security._
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.validation.InProgressForm
import scala.Some
import uk.gov.gds.ier.model.InprogressOrdinary
import play.api.mvc.Results.Redirect
import play.api.mvc.Call
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.PossibleAddress
import play.api.test.FakeApplication
import uk.gov.gds.ier.controller.MockConfig
import uk.gov.gds.ier.config.Config


class StepControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  val mockPreviousCall = mock[Call]
  case class Url[T](url:String) extends NextStep[T] {
    def goToNext(currentState: T) = Redirect(url)
  }

  val mockConfig = new MockConfig

  val testEncryptionService =
    new EncryptionService(new Base64EncodingService, mockConfig)

  def createController(
      form: ErrorTransformForm[InprogressOrdinary],
      theNextStep: NextStep[InprogressOrdinary] = Url("/next-step")) = {
    new OrdinaryStep
      with WithSerialiser
      with WithConfig
      with WithEncryption {

      val serialiser = jsonSerialiser
      val config = mockConfig
      val encryptionService = testEncryptionService

      val routes = Routes(
        get = Call("GET","/get"),
        post = Call("POST","/post"),
        editGet = Call("GET","/editGet"),
        editPost = Call("POST","/editPost")
      )

      def nextStep(currentState: InprogressOrdinary) = theNextStep

      val previousRoute: Option[Call] = Some(mockPreviousCall)
      val validation = form
      def template(form: InProgressForm[InprogressOrdinary], call: Call, backUrl: Option[Call]):Html = Html("This is the template.")
    }
  }

  behavior of "StepController.get"
  it should "return the template page for a valid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controllerMethod = createController(mock[ErrorTransformForm[InprogressOrdinary]]).get
      val request = FakeRequest().withIerSession()

      val result = controllerMethod(request)

      status(result) should be(OK)
      contentAsString(result) should be("This is the template.")
    }
  }

  it should "redirect to the timeout page with invalid session" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val controllerMethod = createController(mock[ErrorTransformForm[InprogressOrdinary]]).get
      val request = FakeRequest().withIerSession(20)

      val result = controllerMethod(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/error/timeout"))
    }
  }

  behavior of "StepController.post"
  it should "bind to the validation and redisplay the template on error" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = ErrorTransformForm(
        mapping("foo" -> text.verifying("Forcing a failure", foo => false))
          (foo => InprogressOrdinary())
          (app => Some("foo")))
      val controllerMethod = createController(form).post

      val request = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "some text")

      val result = controllerMethod(request)

      status(result) should be(OK)
      contentAsString(result) should be("This is the template.")
    }
  }

  it should "bind to the validation and redirect to the next page on successful validation" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val form = ErrorTransformForm(
        mapping("foo" -> text.verifying("I will always pass", foo => true))
          (foo => InprogressOrdinary())
          (app => Some("foo"))
      )
      val controllerMethod = createController(form).post

      val request = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "some text")

      val result = controllerMethod(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/next-step"))
    }
  }

  it should "not allow possibleAddresses in to the session, we don't want to store those, ever!" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {
      val possibleAddress = PartialAddress(addressLine = Some("123 Fake Street"), 
                                           uprn = Some("12345678"),
                                           postcode = "AB12 3CD", 
                                           manualAddress = None)
      val form = ErrorTransformForm(
        mapping("foo" -> text.verifying("I will always pass", foo => true))
          (foo => InprogressOrdinary(
            possibleAddresses = Some(PossibleAddress(
              jsonList = Addresses(List(possibleAddress)),
              postcode = "SW1A 1AA")),
            name = Some(Name("John", None, "Smith"))))
          (app => Some("foo"))
      )
      val request = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "some text")

      val controllerMethod = createController(form).post
      val result =  controllerMethod(request)

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/next-step"))

      cookies(result).get("application") match {
        case Some(cookie) => {
          val cookieIV = cookies(result).get("applicationIV")
          val decryptedInfo = testEncryptionService.decrypt(cookie.value, cookieIV.get.value)
          val application = jsonSerialiser.fromJson[InprogressOrdinary](decryptedInfo)
          application.possibleAddresses should be(None)
        }
        case _ => fail("Should have been able to deserialise")
      }
    }
  }

  it should "bind information from both session and request" in {
    running(FakeApplication(additionalConfiguration = Map("application.secret" -> "test"))) {

      val form = ErrorTransformForm(
        mapping(
          "foo" -> optional(text),
          "bar" -> optional(text)
        ) (
          FooBar.apply
        ) (
          FooBar.unapply
        ) verifying (
          "I will always fail",
          foo => false
        )
      )

      val controller = new StepController[FooBar]
          with WithSerialiser
          with WithConfig
          with WithEncryption {

        def factoryOfT = FooBar(None, None)
        val confirmationRoute = Call("GET", "/confirmation")

        val serialiser = jsonSerialiser
        val config = mockConfig
        val encryptionService = testEncryptionService

        val routes = Routes(
          get = Call("GET","/get"),
          post = Call("POST","/post"),
          editGet = Call("GET","/editGet"),
          editPost = Call("POST","/editPost")
        )

        def nextStep(currentState: FooBar) = {
          Url("/next-step")
        }

        val previousRoute: Option[Call] = Some(Call("GET", "/prev-step"))
        val validation = form
        def template(
            form: InProgressForm[FooBar],
            call: Call,
            backUrl: Option[Call]):Html = {
          val foo = form.form("foo").value
          val bar = form.form("bar").value

          Html(s"Foo is $foo, Bar is $bar")
        }
      }

      val requestOnlyFoo = FakeRequest("POST", "/")
        .withIerSession()
        .withFormUrlEncodedBody("foo" -> "im foo")
      val postMethod = controller.post
      
      val resultOnlyFoo = postMethod(requestOnlyFoo)

      status(resultOnlyFoo) should be (OK)
      contentAsString(resultOnlyFoo) should be("Foo is Some(im foo), Bar is None")

      val requestWithBoth = FakeRequest("POST", "/")
        .withIerSession()
        .withApplication(FooBar(bar = Some("this is bar")))
        .withFormUrlEncodedBody("foo" -> "this is foo")

      val resultWithBoth = postMethod(requestWithBoth)

      status(resultWithBoth) should be(OK)
      contentAsString(resultWithBoth) should be(
        "Foo is Some(this is foo), Bar is Some(this is bar)"
      )
    }
  }
}

case class FooBar(
    foo: Option[String] = None,
    bar: Option[String] = None
) extends InprogressApplication[FooBar] {
  def merge(other: FooBar) = {
    this.copy(
      foo = foo orElse other.foo,
      bar = bar orElse other.bar
    )
  }
}
