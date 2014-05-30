package uk.gov.gds.ier.transaction.crown.nationality

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
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
        FakeRequest(GET, "/register-to-vote/crown/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))

      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("/register-to-vote/crown/nationality")
    }
  }

  behavior of "NationalityController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/date-of-birth"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/nationality")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "redirect to no-franchise page with a country with no right to vote in UK" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/nationality")
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
        FakeRequest(POST, "/register-to-vote/crown/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/crown/nationality")
    }
  }

  it should "display any errors on unsuccessful bind (bad other country)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "BLARGHHUH")
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("This is not a valid country")
      contentAsString(result) should include("/register-to-vote/crown/nationality")
    }
  }

//  behavior of "Completing a prior step when this question is incomplete"
//  it should "stop on this page" in {
//    running(FakeApplication()) {
//      val Some(result) = route(
//        FakeRequest(POST, "/register-to-vote/crown/address")
//          .withIerSession()
//          .withApplication(completeCrownApplication.copy(nationality = None))
//          .withFormUrlEncodedBody(
//          "address.uprn" -> "123456789",
//          "address.postcode" -> "SW1A 1AA"
//        )
//      )
//
//      status(result) should be(SEE_OTHER)
//      redirectLocation(result) should be(Some("/register-to-vote/crown/nationality"))
//    }
//  }

  behavior of "NationalityController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))

      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("/register-to-vote/crown/edit/nationality")
    }
  }

  behavior of "NationalityController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/date-of-birth"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/nationality")
          .withIerSession()
          .withApplication(completeCrownApplication)
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "France")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/confirmation"))
    }
  }

  it should "redirect to no-franchise page with a country with no right to vote in UK" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/nationality")
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
        FakeRequest(POST, "/register-to-vote/crown/edit/nationality").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/crown/edit/nationality")
    }
  }

  it should "display any errors on unsuccessful bind (bad other country)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/nationality")
          .withIerSession()
          .withFormUrlEncodedBody(
            "nationality.british" -> "true",
            "nationality.hasOtherCountry" -> "true",
            "nationality.otherCountries[0]" -> "BLARGHHUH")
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your nationality?")
      contentAsString(result) should include("This is not a valid country")
      contentAsString(result) should include("/register-to-vote/crown/edit/nationality")
    }
  }
}
