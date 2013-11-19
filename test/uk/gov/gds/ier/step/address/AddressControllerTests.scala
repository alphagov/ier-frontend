package uk.gov.gds.ier.step.address

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class AddressControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "AddressController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/address").withIerSession()
      )
      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Question 5")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/nino")
      contentAsString(result) should include("/register-to-vote/address")
    }
  }

  behavior of "AddressController.post"
  it should "bind successfully and redirect to the Previous Address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.address" -> "123 Fake Street", 
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/address")
    }
  }

  behavior of "AddressController.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("/register-to-vote/edit/address")
    }
  }

  behavior of "AddressController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.address" -> "123 Fake Street", 
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/edit/address")
    }
  }
}
