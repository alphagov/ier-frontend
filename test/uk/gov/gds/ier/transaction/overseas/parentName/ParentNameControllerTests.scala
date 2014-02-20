package uk.gov.gds.ier.transaction.overseas.parentName

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{PreviousName, Name, InprogressOrdinary}
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import uk.gov.gds.ier.model.DOB

class ParentNameControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "ParentNameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/parent-name").withIerSession()
      )

      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 6")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/date-left-uk")
      contentAsString(result) should include("Parent or guardian's registration details")
      contentAsString(result) should include("Have they changed their name since they left the UK?")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/parent-name\"")
    }
  }

  behavior of "ParentNameController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parent-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentName.firstName" -> "John",
            "parentName.lastName" -> "Smith",
            "parentPreviousName.hasPreviousName" -> "true",
            "parentPreviousName.previousName.firstName" -> "John",
            "parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }

  it should "bind successfully with no previous name and redirect to last uk address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parent-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentName.firstName" -> "John",
            "parentName.lastName" -> "Smith",
            "parentPreviousName.hasPreviousName" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parent-name")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "parentName.firstName" -> "John",
            "parentName.lastName" -> "Smith",
            "parentPreviousName.hasPreviousName" -> "true",
            "parentPreviousName.previousName.firstName" -> "John",
            "parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/parent-name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Parent or guardian's registration details")
      contentAsString(result) should include("Please enter their full name")
      contentAsString(result) should include("Have they changed their name since they left the UK?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/parent-name\"")
    }
  }

  behavior of "Completing a prior step when this question is incomplete"
  it should "stop on this page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/date-left-uk")
          .withIerSession()
          .withApplication(completeOverseasApplication.copy(dob = Some(DOB(1997,10,10)),
              parentName = None, parentPreviousName = None))
          .withFormUrlEncodedBody(
            "dateLeftUk.month" -> "12",
            "dateLeftUk.year" -> "2010")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/parent-name"))
    }
  }

  behavior of "ParentNameController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/parent-name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 6")
      contentAsString(result) should include("<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/confirmation")
      contentAsString(result) should include("Parent or guardian's registration details")
      contentAsString(result) should include("Have they changed their name since they left the UK?")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/edit/parent-name\"")
    }
  }

  behavior of "ParentNameController.editPost"
  it should "bind successfully and redirect to the last uk address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentName.firstName" -> "John",
            "parentName.lastName" -> "Smith",
            "parentPreviousName.hasPreviousName" -> "true",
            "parentPreviousName.previousName.firstName" -> "John",
            "parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }

  it should "bind successfully with no previous name and redirect to last uk address step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "parentName.firstName" -> "John",
            "parentName.lastName" -> "Smith",
            "parentPreviousName.hasPreviousName" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-uk-address"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "parentName.firstName" -> "John",
            "parentName.lastName" -> "Smith",
            "parentPreviousName.hasPreviousName" -> "true",
            "parentPreviousName.previousName.firstName" -> "John",
            "parentPreviousName.previousName.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/parent-name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Parent or guardian's registration details")
      contentAsString(result) should include("Please enter their full name")
      contentAsString(result) should include("Have they changed their name since they left the UK?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("<form action=\"/register-to-vote/overseas/edit/parent-name\"")
    }
  }  
}
