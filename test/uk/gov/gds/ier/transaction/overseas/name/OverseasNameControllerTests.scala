package uk.gov.gds.ier.transaction.overseas.name

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{PreviousName, Name, InprogressOrdinary}
import akka.util.Timeout
import java.util.concurrent.TimeUnit

class OverseasNameControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "NameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 4")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/last-uk-address")
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/name\"")
    }
  }

  behavior of "NameController.post"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/nino"))
    }
  }

  it should "bind successfully with no previous name and redirect to Nino step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/nino"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/name")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/name\"")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    // enable again when whole page chain is finished for Overseas
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-uk-address")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(name = None, previousName = None))
          .withFormUrlEncodedBody(
            "lastUkAddress.uprn" -> "12345678",
            "lastUkAddress.postcode" -> "SW1A1AA")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  behavior of "NameController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 4")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/confirmation")
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("<form action=\"/register-to-vote/edit/name\"")
    }
  }

  behavior of "NameController.editPost"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nino"))
    }
  }

  it should "bind successfully with no previous name and redirect to Nino step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nino"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/name")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith",
            "previousName.hasPreviousName" -> "true",
            "previousName.previousName.firstName" -> "John",
            "previousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
      contentAsString(result) should include("Have you changed your name in the last 12 months?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/edit/name\"")
    }
  }
}
