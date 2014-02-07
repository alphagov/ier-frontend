package uk.gov.gds.ier.transaction.overseas.passport

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{InprogressOverseas, DOB}

class PassportCheckControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "PassportCheckController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/passport").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 7")
      contentAsString(result) should include(
        "<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/last-uk-address")
      contentAsString(result) should include("Do you have a British Passport?")
      contentAsString(result) should include("/register-to-vote/overseas/passport")
    }
  }

  behavior of "PassportCheckController.post"
  it should "bind successfully (true, true) and redirect to Passport details" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/passport")
          .withIerSession()
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "true",
            "passport.bornInsideUk" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport-details"))
    }
  }

  it should "bind successfully (true, false) and redirect to Passport details" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/passport")
          .withIerSession()
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "true",
            "passport.bornInsideUk" -> "false"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport-details"))
    }
  }

  it should "bind successfully (false, false) and redirect to Citizen details" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/passport")
          .withIerSession()
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "false",
            "passport.bornInsideUk" -> "false"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/citizen-details"))
    }
  }

  it should "bind successfully (false, true, dob < 1983) and redirect to Name step" in {
    running(FakeApplication()) {
      val applicationWithOldDob = InprogressOverseas(
        dob = Some(DOB(1970, 1, 1))
      )

      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/passport")
          .withIerSession()
          .withApplication(applicationWithOldDob)
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "false",
            "passport.bornInsideUk" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully (false, true, dob > 1983) and redirect to Citizen Details" in {
    running(FakeApplication()) {
      val applicationWithYoungDob = InprogressOverseas(
        dob = Some(DOB(1990, 1, 1))
      )

      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/passport")
          .withIerSession()
          .withApplication(applicationWithYoungDob)
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "false",
            "passport.bornInsideUk" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/citizen-details"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/open-register")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "false",
            "passport.bornInsideUk" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  behavior of "PassportCheckController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/passport").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 7")
      contentAsString(result) should include(
        "<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/confirmation")
      contentAsString(result) should include("Do you have a British Passport?")
      contentAsString(result) should include("/register-to-vote/overseas/edit/passport")
    }
  }

  behavior of "PassportCheckController.editPost"
  it should "bind successfully (true, true) and redirect to Passport details" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/passport")
          .withIerSession()
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "true",
            "passport.bornInsideUk" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport-details"))
    }
  }

  it should "bind successfully (true, false) and redirect to Passport details" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/passport")
          .withIerSession()
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "true",
            "passport.bornInsideUk" -> "false"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/passport-details"))
    }
  }

  it should "bind successfully (false, false) and redirect to Citizen details" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/passport")
          .withIerSession()
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "false",
            "passport.bornInsideUk" -> "false"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/citizen-details"))
    }
  }

  it should "bind successfully (false, true, dob < 1983) and redirect to Name step" in {
    running(FakeApplication()) {
      val applicationWithOldDob = InprogressOverseas(
        dob = Some(DOB(1970, 1, 1))
      )

      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/passport")
          .withIerSession()
          .withApplication(applicationWithOldDob)
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "false",
            "passport.bornInsideUk" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/name"))
    }
  }

  it should "bind successfully (false, true, dob > 1983) and redirect to Citizen Details" in {
    running(FakeApplication()) {
      val applicationWithYoungDob = InprogressOverseas(
        dob = Some(DOB(1990, 1, 1))
      )

      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/passport")
          .withIerSession()
          .withApplication(applicationWithYoungDob)
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "false",
            "passport.bornInsideUk" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/citizen-details"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete Application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/open-register")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "passport.hasPassport" -> "false",
            "passport.bornInsideUk" -> "true"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "not display any errors because we are evil dark patterny" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/open-register").withIerSession()
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/ways-to-vote"))
    }
  }
}
