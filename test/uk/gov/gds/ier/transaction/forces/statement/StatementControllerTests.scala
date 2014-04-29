package uk.gov.gds.ier.transaction.forces.statement

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class StatementControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "StatementController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/statement").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 1")
      contentAsString(result) should include("Which of these statements applies to you?")
      contentAsString(result) should include("/register-to-vote/forces/statement")
    }
  }

  behavior of "StatementController.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/statement")
          .withIerSession()
          .withFormUrlEncodedBody( "statement.forcesMember" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/address/first"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/statement")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody( "statement.forcesMember" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/statement").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Which of these statements applies to you?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/forces/statement")
    }
  }

}
