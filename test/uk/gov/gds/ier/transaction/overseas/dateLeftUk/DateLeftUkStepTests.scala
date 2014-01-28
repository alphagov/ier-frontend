package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class DateLeftUkStepTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "DateLeftUkStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/date-left-uk").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 5")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/previously-registered")
      contentAsString(result) should include("When did you leave the UK?")
      contentAsString(result) should include("/register-to-vote/overseas/date-left-uk")
    }
  }

  behavior of "DateLeftUkStep.post"
  it should "bind successfully and redirect to the Registered Address step if month and year are provided" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-uk")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dateLeftUk.month" -> "10",
          "dateLeftUk.year" -> "2000"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/registered-address"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-uk").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("When did you leave the UK?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/date-left-uk")
    }
  }

  behavior of "DateLeftUkStep.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/date-left-uk").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("When did you leave the UK?")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/confirmation")
      contentAsString(result) should include("/register-to-vote/overseas/edit/date-left-uk")
    }
  }

  behavior of "DateLeftUkStep.editPost"
  it should "bind successfully and redirect to the registered address step if year and month are provided" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-uk")
          .withIerSession()
          .withFormUrlEncodedBody(
              "dateLeftUk.month" -> "10",
              "dateLeftUk.year" -> "2000")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/registered-address"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-uk").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("When did you leave the UK?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/edit/date-left-uk")
    }
  }

  behavior of "DateLeftUkStep.post when complete application"
  it should "bind successfully and redirect to the confirmation step if year and month are provided" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-uk")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "dateLeftUk.month" -> "10",
            "dateLeftUk.year" -> "2000")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }


  behavior of "DateLeftUkStep.editPost when complete application"
  it should "bind successfully and redirect to the confirmation step if year and month are provided" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-uk")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "dateLeftUk.month" -> "10",
            "dateLeftUk.year" -> "2000")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

}
