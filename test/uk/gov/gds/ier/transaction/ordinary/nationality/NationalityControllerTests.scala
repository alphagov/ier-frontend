package uk.gov.gds.ier.transaction.ordinary.nationality

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class NationalityControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "NationalityController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/nationality").withIerSession()
      )
      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))

      contentAsString(result) should include("Question 2")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/country-of-residence")
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("/register-to-vote/nationality")
    }
  }

  behavior of "NationalityController.post"
  it should "bind successfully and redirect to the Date Of Birth step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/date-of-birth"))
    }
  }

  it should "redirect to no-franchise page with a country with no right to vote in UK" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/nationality") 
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "Japan")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/no-franchise"))
    }
  }

  it should "display any errors on unsuccessful bind (no content)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("Please select your Nationality")
      contentAsString(result) should include("/register-to-vote/nationality")
    }
  }

  it should "display any errors on unsuccessful bind (bad other country)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "BLARGHHUH")
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("This is not a valid country")
      contentAsString(result) should include("/register-to-vote/nationality")
    }
  }

  behavior of "NationalityController.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("/register-to-vote/edit/nationality")
    }
  }

  behavior of "NationalityController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("Please select your Nationality")
      contentAsString(result) should include("/register-to-vote/edit/nationality")
    }
  }
}
