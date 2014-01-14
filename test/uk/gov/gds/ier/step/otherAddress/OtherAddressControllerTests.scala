package uk.gov.gds.ier.step.otherAddress

import org.specs2.mutable._
import org.specs2.runner.JUnitRunner
import org.junit.runner._
import play.api.test.Helpers._
import play.api.test.{FakeRequest, FakeApplication}
import org.specs2.matcher.Matchers
import uk.gov.gds.ier.test.TestHelpers

@RunWith(classOf[JUnitRunner])
class OtherAddressControllerTests
  extends Specification
  with Matchers
  with TestHelpers {
  "OtherAddressController.get" should {
    "display the page" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(GET, "/register-to-vote/other-address").withIerSession()
        )

        status(result) must be_==(OK)
        contentType(result) must be_==(Some("text/html"))
        contentAsString(result) must contain("Question 8")
        contentAsString(result) must contain("<a class=\"back-to-previous\" href=\"/register-to-vote/previous-address")
        contentAsString(result) must contain("Do you live at a second UK address where you're registered to vote?")
        contentAsString(result) must contain("/register-to-vote/other-address")
      }
    }
  }

  "OtherAddressController.post" should {
    "bind successfully and redirect to the Open Register step" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/other-address")
            .withIerSession()
            .withFormUrlEncodedBody(
              "otherAddress.hasOtherAddress" -> "true"
            )
        )

        status(result) must be_==(SEE_OTHER)
        redirectLocation(result) must be_==(Some("/register-to-vote/open-register"))
      }
    }

    "display any errors on unsuccessful bind" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/other-address").withIerSession()
        )

        status(result) must be_==(OK)
        contentAsString(result) must contain("Do you live at a second UK address where you're registered to vote?")
        contentAsString(result) must contain("Please answer this question")
        contentAsString(result) must contain("/register-to-vote/other-address")
      }
    }
  }

  "OtherAddressController.editGet" should {
    "display the edit page" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(GET, "/register-to-vote/edit/other-address").withIerSession()
        )

        status(result) must be_==(OK)
        contentType(result) must be_==(Some("text/html"))
        contentAsString(result) must contain("Do you live at a second UK address where you're registered to vote?")
        contentAsString(result) must contain("/register-to-vote/edit/other-address")
      }
    }
  }

  "OtherAddressController.editPost" should {
    "bind successfully and redirect to the Confirmation step" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/other-address")
            .withIerSession()
            .withFormUrlEncodedBody(
              "otherAddress.hasOtherAddress" -> "true"
            )
        )

        status(result) must be_==(SEE_OTHER)
        redirectLocation(result) must be_==(Some("/register-to-vote/confirmation"))
      }
    }

    "display any errors on unsuccessful bind" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/other-address").withIerSession()
        )

        status(result) must be_==(OK)
        contentAsString(result) must contain("Do you live at a second UK address where you're registered to vote?")
        contentAsString(result) must contain("Please answer this question")
        contentAsString(result) must contain("/register-to-vote/edit/other-address")
      }
    }
  }
}
