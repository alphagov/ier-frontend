package uk.gov.gds.ier.transaction.complete

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import controllers.routes._
import play.api.test.FakeApplication
import scala.Some

class CompleteControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "CompleteController.get"
  it should "display the page with link back to start when user indicated that has other address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/complete")
          .withFlash(
            "refNum" -> "123457689013",
            "postcode" -> "WR26NJ",  // postcode is not currently shown
            "hasOtherAddress" -> "true",
            "backToStartUrl" -> "/register-to-vote/start",
            "showEmailConfirmation" -> "true")
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      val renderedOutput = contentAsString(result)

      renderedOutput should include("123457689013")
      renderedOutput should include("/register-to-vote/start")
    }
  }

  it should "display the page without link back to start when user did not indicated other address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/complete")
          .withFlash(
            "refNum" -> "123457689013",
            "postcode" -> "WR26NJ",
            "hasOtherAddress" -> "false",
            "backToStartUrl" -> "/register-to-vote/start",
            "showEmailConfirmation" -> "true")
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      val renderedOutput = contentAsString(result)

      renderedOutput should include("123457689013")
      renderedOutput should not include("/register-to-vote/start")
    }
  }

  it should "display the page with email confirmation info" in runningApp {
    val Some(result) = route(
      FakeRequest(GET, "/register-to-vote/complete")
        .withFlash(
          "refNum" -> "123457689013",
          "postcode" -> "WR26NJ",
          "hasOtherAddress" -> "false",
          "backToStartUrl" -> "/register-to-vote/start",
          "showEmailConfirmation" -> "true")
        .withIerSession()
    )

    status(result) should be(OK)
    contentType(result) should be(Some("text/html"))
    val renderedOutput = contentAsString(result)

    renderedOutput should include("We have sent you a confirmation email.")
  }

  it should "display the page without email confirmation info" in runningApp {
    val Some(result) = route(
      FakeRequest(GET, "/register-to-vote/complete")
        .withFlash(
          "refNum" -> "123457689013",
          "postcode" -> "WR26NJ",
          "hasOtherAddress" -> "false",
          "backToStartUrl" -> "/register-to-vote/start",
          "showEmailConfirmation" -> "false")
        .withIerSession()
    )

    status(result) should be(OK)
    contentType(result) should be(Some("text/html"))
    val renderedOutput = contentAsString(result)

    renderedOutput should not include("We have sent you a confirmation email.")
  }


}
