package uk.gov.gds.ier.transaction.country

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

@RunWith(classOf[JUnitRunner])
class CountryControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "CountryController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/country-of-residence").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 1")
      contentAsString(result) should not include("<a class=\"back-to-previous\"")
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("/register-to-vote/country-of-residence")
    }
  }

  behavior of "CountryController.post"
  it should "bind successfully and redirect to the Nationality step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "England"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/nationality"))
    }
  }

  it should "bind successfully on Northern Ireland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "Northern Ireland"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/northern-ireland"))
    }
  }

  it should "bind successfully on Scotland and redirect to the exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence")
          .withIerSession()
          .withFormUrlEncodedBody(
          "country.residence" -> "Scotland"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/scotland"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/country-of-residence").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Where do you live?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/country-of-residence")
    }
  }
}
