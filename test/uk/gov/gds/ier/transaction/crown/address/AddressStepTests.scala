package uk.gov.gds.ier.transaction.crown.address

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.LastUkAddress
import akka.util.Timeout
import java.util.concurrent.TimeUnit

class AddressStepTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "AddressStep.get"
  it should "display the page with last uk address (None value)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Question 2")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address/lookup\"")
    }
  }

  it should "display the page with last uk address (false value)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/address")
          .withApplication(completeCrownApplication.copy(
            address = Some(LastUkAddress(
              hasUkAddress = Some(false),
              address = None
            ))
          ))
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Question 2")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address/lookup\"")
    }
  }

  it should "display the page with current uk address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/address")
          .withApplication(completeCrownApplication.copy(
          address = Some(LastUkAddress(
            hasUkAddress = Some(true),
            address = None
          ))
        ))
        .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What is your UK address?"
      )
      contentAsString(result) should include("Question 2")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address/lookup\"")
    }
  }

  behavior of "AddressStep.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.uprn" -> "123456789",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/previous-address"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.manualAddress.lineTwo" -> "Moseley Road",
            "address.manualAddress.lineThree" -> "Hallow",
            "address.manualAddress.city" -> "Worcester",
            "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/previous-address"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.manualAddress.lineTwo" -> "Moseley Road",
            "address.manualAddress.lineThree" -> "Hallow",
            "address.manualAddress.city" -> "Worcester",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      implicit def defaultAwaitTimeout = Timeout(10, TimeUnit.SECONDS)
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address/lookup\"")

    }
  }

behavior of "AddressStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Question 2")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address/lookup\"")

    }
  }

  behavior of "AddressStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.uprn" -> "123456789",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/previous-address"))
    }
  }

  it should "bind successfully and redirect to the next step with a manual address" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/address")
          .withIerSession()
          .withFormUrlEncodedBody(
            "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.manualAddress.lineTwo" -> "Moseley Road",
            "address.manualAddress.lineThree" -> "Hallow",
            "address.manualAddress.city" -> "Worcester",
            "address.postcode" -> "SW1A 1AA"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/previous-address"))
    }
  }

  it should "bind successfully and redirect to confirmation if all other steps are complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/address")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "address.manualAddress.lineOne" -> "Unit 4, Elgar Business Centre",
            "address.manualAddress.lineTwo" -> "Moseley Road",
            "address.manualAddress.lineThree" -> "Hallow",
            "address.manualAddress.city" -> "Worcester",
            "address.postcode" -> "SW1A 1AA"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/address").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "What was your last UK address?"
      )
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/crown/address/lookup\"")

    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/address/first")
          .withIerSession()
          .withApplication(completeCrownApplication.copy(address = None))
          .withFormUrlEncodedBody(
            "address.hasUkAddress" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/address"))
    }
  }
}
