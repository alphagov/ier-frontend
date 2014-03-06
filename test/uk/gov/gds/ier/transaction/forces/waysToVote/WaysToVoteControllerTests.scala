package uk.gov.gds.ier.transaction.forces.waysToVote

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.test.TestHelpers
import org.scalatest.mock.MockitoSugar
import play.api.test.Helpers._
import scala.Some
import play.api.test.{FakeRequest, FakeApplication}


class WaysToVoteControllerTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  val serialiser = jsonSerialiser

  behavior of "WaysToVoteController.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/ways-to-vote").withIerSession()
      )
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 11")
      contentAsString(result) should include("How do you want to vote?")
    }
  }

  behavior of "WaysToVoteController.post"
  it should "redirect to postal vote step when submitted data indicate by-post way" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/ways-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "by-post")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/postal-vote"))
    }
  }

  behavior of "WaysToVoteController.post"
  it should "redirect to postal vote step when submitted data indicate by-proxy way" in {
    // proxy voting is handled by the same controller as postal vote, hence same next step URL
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/ways-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "by-proxy")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/proxy-vote"))
    }
  }

  behavior of "WaysToVoteController.post"
  it should "redirect to Contact Step when submitted data indicate in-person way" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/ways-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "in-person")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/contact"))
    }
  }

  behavior of "WaysToVoteController.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/forces/edit/ways-to-vote").withIerSession()
      )
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 11")
      contentAsString(result) should include("How do you want to vote?")
    }
  }

  behavior of "WaysToVoteController.editPost"
  it should "redirect to postal vote step when submitted data indicate by-post way" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/ways-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "by-post")
      )
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/postal-vote"))
    }
  }

  it should "bind successfully and redirect to the confirmation step with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/ways-to-vote")
          .withIerSession()
          .withApplication(completeForcesApplication)
          .withFormUrlEncodedBody(
            "waysToVote.wayType" -> "by-post")
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/forces/confirmation"))
    }
  }

  behavior of "WaysToVoteController.get"
  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/ways-to-vote").withIerSession()
      )
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 11")
      contentAsString(result) should include("How do you want to vote?")

      contentAsString(result) should include("Please answer this question")
    }
  }

  behavior of "WaysToVoteController.editGet"
  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/forces/edit/ways-to-vote").withIerSession()
      )
      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 11")
      contentAsString(result) should include("How do you want to vote?")

      contentAsString(result) should include("Please answer this question")
    }
  }
}
