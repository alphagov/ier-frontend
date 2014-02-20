package uk.gov.gds.ier.transaction.overseas.dateLeftUk

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{LastRegisteredToVote, LastRegisteredType, DOB, DateOfBirth}
import uk.gov.gds.ier.model.LastRegisteredType._
import play.api.test.FakeApplication
import scala.Some

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
      contentAsString(result) should include(
        "<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/previously-registered"
      )
      contentAsString(result) should include("When did you leave the UK?")
      contentAsString(result) should include("/register-to-vote/overseas/date-left-uk")
    }
  }

  behavior of "DateLeftUkStep.post"
  it should "bind successfully and redirect to the LastUKAddress step" in {
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
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }
  
    it should "bind successfully and redirect to the ParentName step if the application's age is less than 18 and has left uk less than 15 years" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-uk")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(
            dob = Some(DOB(1997,10,10)),
            parentName = None, parentPreviousName = None))
          .withFormUrlEncodedBody(
          "dateLeftUk.month" -> "10",
          "dateLeftUk.year" -> "2010"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/parent-name"))
    }
  }

  it should "bind successfully and exit if it's been over 15 years when the user left the UK" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-uk")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dateLeftUk.month" -> "10",
          "dateLeftUk.year" -> "1998"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(
        Some("/register-to-vote/exit/overseas/left-uk-over-15-years")
      )
    }
  }

  it should "bind successfully and redirect to the exit page if he/she was too old" +
    "when he/she left the UK and was never registered before" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-uk")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(
            dob = Some(DOB(1982,10,10)),
            lastRegisteredToVote = Some(LastRegisteredToVote(LastRegisteredType.NotRegistered))))
          .withFormUrlEncodedBody(
          "dateLeftUk.month" -> "10",
          "dateLeftUk.year" -> "2001"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(
        Some("/register-to-vote/exit/overseas/too-old-when-left-uk")
      )
    }
  }

  it should "bind successfully and redirect the Registered Address step if he/she was not too" +
    "old (<=18) when he/she left the UK and was never registered before" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-uk")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(
            lastUkAddress = None,
            dob = Some(DOB(1983,10,10)),
            lastRegisteredToVote = Some(LastRegisteredToVote(LastRegisteredType.NotRegistered))))
          .withFormUrlEncodedBody(
          "dateLeftUk.month" -> "10",
          "dateLeftUk.year" -> "2001"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
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
      contentAsString(result) should include(
        "<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/confirmation"
      )
      contentAsString(result) should include("/register-to-vote/overseas/edit/date-left-uk")
    }
  }

  behavior of "DateLeftUkStep.editPost"
  it should "bind successfully and redirect to the lastUkAddress" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-uk")
          .withIerSession()
          .withFormUrlEncodedBody(
              "dateLeftUk.month" -> "10",
              "dateLeftUk.year" -> "2000")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }

  it should "bind successfully and redirect to the exit page if he/she was too old when" +
    "he/she left the UK and was never registered before" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-uk")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(
            dob = Some(DOB(1982,10,10)),
            lastRegisteredToVote = Some(LastRegisteredToVote(LastRegisteredType.NotRegistered))))
          .withFormUrlEncodedBody(
          "dateLeftUk.month" -> "10",
          "dateLeftUk.year" -> "2001"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(
        Some("/register-to-vote/exit/overseas/too-old-when-left-uk")
      )
    }
  }

  it should "bind successfully and redirect the Registered Address step if he/she was not too" +
    "old (<=18) when he/she left the UK and was never registered before" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-uk")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(
            lastUkAddress = None,
            dob = Some(DOB(1983,10,10)),
            lastRegisteredToVote = Some(LastRegisteredToVote(LastRegisteredType.NotRegistered))))
          .withFormUrlEncodedBody(
          "dateLeftUk.month" -> "10",
          "dateLeftUk.year" -> "2001"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }

  it should "bind successfully and exit if it's been over 15 years when the user left the UK" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/date-left-uk")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dateLeftUk.month" -> "10",
          "dateLeftUk.year" -> "1998"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(
        Some("/register-to-vote/exit/overseas/left-uk-over-15-years")
      )
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
  it should "bind successfully and redirect to the confirmation step if all steps complete" in {
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
  it should "bind successfully and redirect to the confirmation step if all steps complete" in {
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
