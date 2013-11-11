package uk.gov.gds.ier.step.address

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

@RunWith(classOf[JUnitRunner])
class OtherAddressControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "OtherAddressController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/other-address").withIerSession()
      )
      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Do you live at a second UK address where you&#x27;re registered to vote?")
      contentAsString(result) should include("/register-to-vote/other-address")
    }
  }

  behavior of "OtherAddressController.post"
  it should "bind successfully and redirect to the Open Register step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/other-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "otherAddress.hasOtherAddress" -> "true" 
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/other-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Do you live at a second UK address where you&#x27;re registered to vote?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/other-address")
    }
  }

  behavior of "OtherAddressController.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/other-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Do you live at a second UK address where you&#x27;re registered to vote?")
      contentAsString(result) should include("/register-to-vote/edit/other-address")
    }
  }

  behavior of "OtherAddressController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/other-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "otherAddress.hasOtherAddress" -> "true" 
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/other-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Do you live at a second UK address where you&#x27;re registered to vote?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/edit/other-address")
    }
  }
}
