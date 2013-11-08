package uk.gov.gds.ier.step.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

@RunWith(classOf[JUnitRunner])
class PreviousAddressControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "PreviousAddressController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/previous-address").withIerSession()
      )
      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Have you moved within the last 12 months?")
      contentAsString(result) should include("/register-to-vote/previous-address")
    }
  }

  behavior of "PreviousAddressController.post"
  it should "bind successfully and redirect to the Other Address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "true",
            "previousAddress.previousAddress.address" -> "123 Fake Street", 
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/other-address"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Have you moved within the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/previous-address")
    }
  }

  behavior of "PreviousAddressController.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Have you moved within the last 12 months?")
      contentAsString(result) should include("/register-to-vote/edit/previous-address")
    }
  }

  behavior of "PreviousAddressController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "true",
            "previousAddress.previousAddress.address" -> "123 Fake Street", 
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Have you moved within the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/edit/previous-address")
    }
  }
}
