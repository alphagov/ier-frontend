package uk.gov.gds.ier.transaction.forces.address

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
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
        FakeRequest(GET, "/register-to-vote/forces/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What is your UK address?"
      )
      contentAsString(result) should include("Question 2")
      contentAsString(result) should include("/register-to-vote/forces/address")
    }
  }

  behavior of "AddressStep.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.uprn" -> "123456789",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.manualAddress" -> "123 Fake Street",
            "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "address.manualAddress" -> "123 Fake Street",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What is your UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/address")
    }
  }

behavior of "AddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What is your UK address?"
      )
      contentAsString(result) should include("Question 2")
      contentAsString(result) should include(
        "<a class=\"back-to-previous\" href=\"/register-to-vote/forces/confirmation"
      )
      contentAsString(result) should include("/register-to-vote/forces/address/lookup")
    }
  }

  behavior of "AddressStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.uprn" -> "123456789",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.manualAddress" -> "123 Fake Street",
            "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nationality"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "address.manualAddress" -> "123 Fake Street",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What is your UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/address/lookup")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  ignore should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/statement")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(address = None))
          .withFormUrlEncodedBody(
            "statement.forcesMember" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/address"))
    }
  }
}
