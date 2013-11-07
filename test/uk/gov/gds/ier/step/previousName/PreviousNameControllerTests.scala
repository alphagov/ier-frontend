package uk.gov.gds.ier.step.previousName

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

@RunWith(classOf[JUnitRunner])
class PreviousNameControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "PreviousNameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/previous-name").withIerSession()
      )
      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("/register-to-vote/previous-name")
    }
  }

  behavior of "PreviousNameController.post"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John", 
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nino"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/previous-name")
    }
  }

  behavior of "PreviousNameController.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/previous-name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("/register-to-vote/edit/previous-name")
    }
  }

  behavior of "PreviousNameController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John", 
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/edit/previous-name")
    }
  }
}
