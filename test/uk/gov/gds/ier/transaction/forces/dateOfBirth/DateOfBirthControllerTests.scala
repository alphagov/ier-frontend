package uk.gov.gds.ier.transaction.forces.dateOfBirth

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import org.joda.time.DateTime

class DateOfBirthControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "DateOfBirthController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/date-of-birth").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 5")
      contentAsString(result) should include("What is your date of birth?")
      contentAsString(result) should include("/register-to-vote/forces/date-of-birth")
    }
  }

  behavior of "DateOfBirthController.post"
  it should "bind successfully and redirect to the Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.dob.day" -> "1",
          "dob.dob.month" -> "1",
          "dob.dob.year" -> "1970")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/name"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when all complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/date-of-birth")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
          "dob.dob.day" -> "1",
          "dob.dob.month" -> "1",
          "dob.dob.year" -> "1970")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  behavior of "DateOfBirthController.post"
  it should "bind successfully and redirect too young exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.dob.day" -> "1",
          "dob.dob.month" -> "1",
          "dob.dob.year" -> s"${DateTime.now.getYear - 10}")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/too-young"))
    }
  }

  it should "bind successfully and push to under18 exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.noDob.reason" -> "I was never born",
          "dob.noDob.range" -> "under18"
        )
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/under-18"))
    }
  }
  it should "bind successfully and push to dont-know exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.noDob.reason" -> "I was never born",
          "dob.noDob.range" -> "dontKnow"
        )
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/dont-know"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/date-of-birth").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your date of birth?")
      contentAsString(result) should include("Please enter your date of birth")
      contentAsString(result) should include("/register-to-vote/forces/date-of-birth")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/nationality")
          .withIerSession()
          .withApplication(completeForcesApplication.copy(dob = None))
          .withFormUrlEncodedBody(
          "nationality.british" -> "true"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/date-of-birth"))
    }
  }

  behavior of "DateOfBirthController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/date-of-birth").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 5")
      contentAsString(result) should include("What is your date of birth?")
      contentAsString(result) should include("/register-to-vote/forces/edit/date-of-birth")
    }
  }

  behavior of "DateOfBirthController.editPost"
  it should "bind successfully and redirect to the Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.dob.day" -> "1",
          "dob.dob.month" -> "1",
          "dob.dob.year" -> "1970")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/name"))
    }
  }

  it should "bind successfully and redirect to the confirmation step when all complete" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/date-of-birth")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
          "dob.dob.day" -> "1",
          "dob.dob.month" -> "1",
          "dob.dob.year" -> "1970")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "bind successfully and redirect too young exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.dob.day" -> "1",
          "dob.dob.month" -> "1",
          "dob.dob.year" -> s"${DateTime.now.getYear - 10}")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/too-young"))
    }
  }

  it should "bind successfully and push to under18 exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.noDob.reason" -> "I was never born",
          "dob.noDob.range" -> "under18"
        )
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/under-18"))
    }
  }
  it should "bind successfully and push to dont-know exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/date-of-birth")
          .withIerSession()
          .withFormUrlEncodedBody(
          "dob.noDob.reason" -> "I was never born",
          "dob.noDob.range" -> "dontKnow"
        )
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/exit/dont-know"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/date-of-birth").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your date of birth?")
      contentAsString(result) should include("Please enter your date of birth")
      contentAsString(result) should include("/register-to-vote/forces/edit/date-of-birth")
    }
  }
}
