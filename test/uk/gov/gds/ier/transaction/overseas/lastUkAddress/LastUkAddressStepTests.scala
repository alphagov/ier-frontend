package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class LastUkAddressStepTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "LastUkAddressStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/last-uk-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("Question 5 or 6")
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address")
    }
  }

  behavior of "LastUkAddressStep.post"
  it should "bind successfully and redirect to the Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully and redirect to the Name step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress" -> "123 Fake Street",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress" -> "123 Fake Street",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address")
    }
  }

behavior of "LastUkAddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/last-uk-address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("Question 5 or 6")
      contentAsString(result) should include(
        "<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/confirmation"
      )
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address/lookup")
    }
  }

  behavior of "LastUkAddressStep.editPost"
  it should "bind successfully and redirect to the Previous Address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-uk-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "123456789",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully and redirect to the Name step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-uk-address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress" -> "123 Fake Street",
            "lastUkAddress.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-uk-address")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "lastUkAddress.manualAddress" -> "123 Fake Street",
            "lastUkAddress.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-uk-address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was the UK address where you were last registered to vote?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/last-uk-address/lookup")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  ignore should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previously-registered")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(lastUkAddress = None))
          .withFormUrlEncodedBody(
            "previouslyRegistered.hasPreviouslyRegistered" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }
}
