package uk.gov.gds.ier.transaction.ordinary.address

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class AddressStepTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "AddressStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Question 6")
      contentAsString(result) should include("/register-to-vote/address")
    }
  }

  behavior of "AddressStep.post"
  it should "redirect to the next step (Other Address) when selected address is provided" in {
    // test that select address step is skipped, from postcode page directly to the next step
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.uprn" -> "123456789",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/other-address"))
    }
  }

  it should "redirect to the next step (Other Address) when manual address is provided" in {
    // test that manual address step is skipped, from postcode page directly to the next step
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.manualAddress" -> "123 Fake Street",
            "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/other-address"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "address.manualAddress" -> "123 Fake Street",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind and stay on postcode page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/address/lookup\"")
      // postcode page is a rare page where post action is different from page URL
    }
  }

  behavior of "AddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Question 6")
      contentAsString(result) should include("<form action=\"/register-to-vote/address/lookup\"")
    }
  }

  behavior of "AddressStep.editPost"
  it should "bind successfully and redirect to the Other Address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.uprn" -> "123456789",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/other-address"))
    }
  }

  it should "bind successfully and redirect to the Other Address step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.manualAddress" -> "123 Fake Street",
            "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/other-address"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/address")
          .withIerSession()
          .withApplication(completeOrdinaryApplication)
          .withFormUrlEncodedBody(
            "address.manualAddress" -> "123 Fake Street",
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
      contentAsString(result) should include("<form action=\"/register-to-vote/address/lookup\"")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withApplication(completeOrdinaryApplication.copy(address = None))
          .withFormUrlEncodedBody(
            "country.residence" -> "England"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/address"))
    }
  }
}
