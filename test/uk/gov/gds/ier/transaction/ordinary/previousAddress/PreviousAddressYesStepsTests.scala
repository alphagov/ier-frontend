package uk.gov.gds.ier.transaction.ordinary.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.PartialPreviousAddress

class PreviousAddressYesStepsTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "PreviousAddressPostcodeController"
  it should "display the page on GET" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "Have you moved out from another UK address in the last 12 months?"
      )
      contentAsString(result) should include("Question 8 of 11")
      contentAsString(result) should include("<form action=\"/register-to-vote/previous-address/lookup\"")
    }
  }

  it should "redirect to next step on POST with all required data" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/postcode")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address/select"))
    }
  }

  it should "stay on same postcode page and display errors on POST with missing required data" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Have you moved out from another UK address in the last 12 months?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("/register-to-vote/previous-address/lookup")
    }
  }

  behavior of "PreviousAddressSelectController"

  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.uprn" -> "123456789",
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/manual")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.manualAddress" -> "123 Fake Street",
            "previousAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address/manual")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "previousAddress.manualAddress" -> "123 Fake Street",
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  behavior of "PreviousAddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "Have you moved out from another UK address in the last 12 months?"
      )
      contentAsString(result) should include("Question 8 of 11")
      contentAsString(result) should include("<form action=\"/register-to-vote/previous-address/lookup\"")
    }
  }

  behavior of "PreviousAddressStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.uprn" -> "123456789",
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/manual")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.manualAddress" -> "123 Fake Street",
            "previousAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/open-register"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/manual")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "previousAddress.manualAddress" -> "123 Fake Street",
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Have you moved out from another UK address in the last 12 months?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("/register-to-vote/previous-address/lookup")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/previous-address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(previousAddress = None))
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-address/postcode"))
    }
  }
}
