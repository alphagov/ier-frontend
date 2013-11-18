package uk.gov.gds.ier.step.openRegister

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class OpenRegisterControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "OpenRegisterController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/open-register").withIerSession()
      )
      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Do you want to remove your name and address from the open register?")
      contentAsString(result) should include("/register-to-vote/open-register")
    }
  }

  behavior of "OpenRegisterController.post"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/open-register")
          .withIerSession()
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/postal-vote"))
    }
  }

  it should "not display any errors because we are evil dark patterny" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/open-register").withIerSession()
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/postal-vote"))
    }
  }

  behavior of "OpenRegisterController.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/open-register").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Do you want to remove your name and address from the open register?")
      contentAsString(result) should include("/register-to-vote/edit/open-register")
    }
  }

  behavior of "OpenRegisterController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/open-register")
          .withIerSession()
          .withFormUrlEncodedBody("openRegister.optIn" -> "true")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "not display any errors because we are evil dark patterny" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/open-register").withIerSession()
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }
}
