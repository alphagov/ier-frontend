package uk.gov.gds.ier.transaction.crown.applicationFormVote

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers

class CrownPostalVoteStepTests
  extends FlatSpec
  with Matchers
  with MockitoSugar
  with TestHelpers {

  behavior of "PostalVoteStep.get"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/postal-vote").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 12")
      contentAsString(result) should include(
        "Do you want us to send you a postal vote application form?")
      contentAsString(result) should include("/register-to-vote/crown/postal-vote")
    }
  }

  behavior of "PostalVoteStep.post"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/postal-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "postalOrProxyVote.optIn" -> "true",
            "postalOrProxyVote.deliveryMethod.methodName" -> "email",
            "postalOrProxyVote.deliveryMethod.emailAddress" -> "mail@test.co.uk",
            "postalOrProxyVote.voteType" -> "by-post"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/contact"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/postal-vote").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Do you want us to send you a postal vote application form?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/crown/postal-vote")
    }
  }

  behavior of "PostalVoteStep.editGet"
  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/crown/edit/postal-vote").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include("Question 12")
      contentAsString(result) should include(
        "Do you want us to send you a postal vote application form?")
      contentAsString(result) should include("/register-to-vote/crown/edit/postal-vote")
    }
  }

  behavior of "PostalVoteStep.editPost"
  it should "bind successfully and redirect to the next step" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/postal-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "postalOrProxyVote.optIn" -> "true",
            "postalOrProxyVote.deliveryMethod.methodName" -> "email",
            "postalOrProxyVote.deliveryMethod.emailAddress" -> "mail@test.co.uk",
            "postalOrProxyVote.voteType" -> "by-post"
        )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/crown/contact"))
    }
  }

  it should "display any errors on unsuccessful bind" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/crown/edit/postal-vote").withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include(
        "Do you want us to send you a postal vote application form?")
      contentAsString(result) should include("Please answer this question")
      contentAsString(result) should include("/register-to-vote/crown/edit/postal-vote")
    }
  }
}