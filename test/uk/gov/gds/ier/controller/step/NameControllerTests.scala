package uk.gov.gds.ier.controller.step

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.joda.time.DateTime
import play.api.test._
import play.api.test.Helpers._
import play.api.mvc.Cookie

@RunWith(classOf[JUnitRunner])
class NameControllerTests extends FlatSpec with Matchers with MockitoSugar {

  behavior of "NameController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/name")
          .withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(3).toString())
        )
      )
      
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your full name?")
    }
  }

  behavior of "NameController.post"
  it should "bind successfully and redirect to the Previous Name step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/name")
          .withFormUrlEncodedBody("name.firstName" -> "John", "name.lastName" -> "Smith")
          .withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(3).toString())
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/previous-name"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/name")
          .withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(3).toString())
        )
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
    }
  }

  behavior of "NameController.editGet"
  it should "display the edit page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/edit/name")
          .withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(3).toString())
        )
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("What is your full name?")
    }
  }

  behavior of "NameController.editPost"
  it should "bind successfully and redirect to the Confirmation step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name")
          .withFormUrlEncodedBody("name.firstName" -> "John", "name.lastName" -> "Smith")
          .withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(3).toString())
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/confirmation"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/edit/name")
          .withCookies(Cookie("sessionKey", DateTime.now.minusMinutes(3).toString())
        )
      )

      status(result) should be(OK)
      contentAsString(result) should include("What is your full name?")
      contentAsString(result) should include("Please enter your full name")
    }
  }
}
