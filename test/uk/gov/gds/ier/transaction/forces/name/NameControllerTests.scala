package uk.gov.gds.ier.transaction.forces.name

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class NameControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "NameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 6")
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/name\"")
    }
  }

  behavior of "NameController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nino"))
    }
  }



  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/name")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/name\"")
    }
  }

  behavior of "NameController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/name").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 6")
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/name\"")
    }
  }

  behavior of "NameController.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/name")
          .withIerSession()
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/nino"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/name")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "name.firstName" -> "John",
            "name.lastName" -> "Smith")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/name").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
      contentAsString(result) should include("<form action=\"/register-to-vote/forces/edit/name\"")
    }
  }
}
