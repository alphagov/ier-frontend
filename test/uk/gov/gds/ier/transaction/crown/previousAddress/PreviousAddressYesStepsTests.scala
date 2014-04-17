package uk.gov.gds.ier.transaction.crown.previousAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class PreviousAddressYesStepsTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "PreviousAddressPostcodeController"
  it should "display the page on GET" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your previous UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/previous-address/postcode\"")
    }
  }

  it should "redirect to next step on POST with all required data" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address/postcode")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/previous-address/select"))
    }
  }

  it should "stay on same postcode page and display errors on POST with missing required data" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your previous UK address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("/register-to-vote/crown/previous-address")
    }
  }

  behavior of "PreviousAddressSelectController"

  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.uprn" -> "123456789",
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address/manual")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "previousAddress.manualAddress.lineTwo" -> "Moseley Road",
            "previousAddress.manualAddress.lineThree" -> "Hallow",
            "previousAddress.manualAddress.city" -> "Worcester",
            "previousAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address/manual")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "previousAddress.manualAddress.lineTwo" -> "Moseley Road",
            "previousAddress.manualAddress.lineThree" -> "Hallow",
            "previousAddress.manualAddress.city" -> "Worcester",
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  behavior of "PreviousAddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your previous UK address?"
      )
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/edit/previous-address/postcode\"")
    }
  }

  behavior of "PreviousAddressStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/select")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.uprn" -> "123456789",
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/manual")
          .withIerSession()
          .withFormUrlEncodedBody(
            "previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "previousAddress.manualAddress.lineTwo" -> "Moseley Road",
            "previousAddress.manualAddress.lineThree" -> "Hallow",
            "previousAddress.manualAddress.city" -> "Worcester",
            "previousAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/manual")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "previousAddress.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "previousAddress.manualAddress.lineTwo" -> "Moseley Road",
            "previousAddress.manualAddress.lineThree" -> "Hallow",
            "previousAddress.manualAddress.city" -> "Worcester",
            "previousAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/previous-address/postcode").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your previous UK address?"
      )
      contentAsString(result) should include("Please enter your postcode")
      contentAsString(result) should include("/register-to-vote/crown/edit/previous-address")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/previous-address")
          .withIerSession()
          .withApplication(completeCrownApplication.copy(previousAddress = None))
          .withFormUrlEncodedBody(
            "previousAddress.movedRecently" -> "yes"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/previous-address/postcode"))
    }
  }
}
