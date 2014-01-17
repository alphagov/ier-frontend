package uk.gov.gds.ier.transaction.ordinary.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

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
      contentAsString(result) should include("Question 7")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/address")
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
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/other-address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "true",
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "bind successfully and redirect to the Other Address step with a manual address" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/previous-address")
            .withIerSession()
            .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "true",
            "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
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

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(previousAddress = None))
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address"))
    }
  }

  behavior of "PreviousAddressController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 7")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/confirmation")
      contentAsString(result) should include("Have you moved within the last 12 months?")
      contentAsString(result) should include("/register-to-vote/edit/previous-address")
    }
  }

  behavior of "PreviousAddressController.editPost"
  it should "bind successfully and redirect to the Other Address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "true",
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/other-address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "true",
            "previousAddress.previousAddress.uprn" -> "123456789",
            "previousAddress.previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "bind successfully and redirect to the Other Address step with a manual address" in {
      running(FakeApplication()) {
        val Some(result) = route(
          FakeRequest(POST, "/register-to-vote/edit/previous-address")
            .withIerSession()
            .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "true",
            "previousAddress.previousAddress.manualAddress" -> "123 Fake Street",
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
        FakeRequest(POST, "/register-to-vote/edit/previous-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Have you moved within the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/edit/previous-address")
    }
  }

}
