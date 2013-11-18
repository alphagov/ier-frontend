package uk.gov.gds.ier.step.nino

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class NinoControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "NinoController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/nino").withIerSession()
      )
      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("/register-to-vote/nino")
    }
  }

  behavior of "NinoController.post"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/nino")
          .withIerSession()
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/address"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/nino").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("Please enter your National Insurance number")
      contentAsString(result) should include("/register-to-vote/nino")
    }
  }

  behavior of "NinoController.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/nino").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("/register-to-vote/edit/nino")
    }
  }

  behavior of "NinoController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/nino")
          .withIerSession()
          .withFormUrlEncodedBody("NINO.NINO" -> "AB 12 34 56 D")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/nino").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your National Insurance number?")
      contentAsString(result) should include("Please enter your National Insurance number")
      contentAsString(result) should include("/register-to-vote/edit/nino")
    }
  }
}
