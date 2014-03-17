package uk.gov.gds.ier.transaction.overseas.previouslyRegistered

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import org.joda.time.DateTime

class PreviouslyRegisteredStepTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "PreviouslyRegisteredStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/previously-registered").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Was your previous registration as an overseas voter?")
      contentAsString(result) should include("/register-to-vote/overseas/previously-registered")
    }
  }

  behavior of "PreviouslyRegisteredStep.post"
  it should "bind successfully and redirect to the First Time step if I answer no" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previously-registered")
          .withIerSession()
          .withFormUrlEncodedBody(
          "previouslyRegistered.hasPreviouslyRegistered" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-registered-to-vote"))
    }
  }

  it should "bind successfully and redirect to the Date Left UK step if I answer yes" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previously-registered")
          .withIerSession()
          .withFormUrlEncodedBody(
          "previouslyRegistered.hasPreviouslyRegistered" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-uk"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previously-registered").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Was your previous registration as an overseas voter?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/previously-registered")
    }
  }

  behavior of "PreviouslyRegisteredStep.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/previously-registered").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Was your previous registration as an overseas voter?")
      contentAsString(result) should include("/register-to-vote/overseas/edit/previously-registered")
    }
  }

  behavior of "PreviouslyRegisteredStep.editPost"
  it should "bind successfully and redirect to the first time registering step if I answer no" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/previously-registered")
          .withIerSession()
          .withFormUrlEncodedBody(
          "previouslyRegistered.hasPreviouslyRegistered" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/last-registered-to-vote"))
    }
  }

  it should "bind successfully and redirect to the date left uk step if I answer yes" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/previously-registered")
          .withIerSession()
          .withFormUrlEncodedBody(
          "previouslyRegistered.hasPreviouslyRegistered" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-uk"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/previously-registered").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Was your previous registration as an overseas voter?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/overseas/edit/previously-registered")
    }
  }

  behavior of "PreviouslyRegisteredStep.post when complete application"
  it should "bind successfully and redirect to the confirmation step if I answer no" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previously-registered")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
          "previouslyRegistered.hasPreviouslyRegistered" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "bind successfully and redirect to the confirmation step if I answer yes" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/previously-registered")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
          "previouslyRegistered.hasPreviouslyRegistered" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  behavior of "PreviouslyRegisteredStep.editPost when complete application"
  it should "bind successfully and redirect to the confirmation step if I answer no" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/previously-registered")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
          "previouslyRegistered.hasPreviouslyRegistered" -> "false")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "bind successfully and redirect to the confirmation step if I answer yes" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/previously-registered")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
          "previouslyRegistered.hasPreviouslyRegistered" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }
}
