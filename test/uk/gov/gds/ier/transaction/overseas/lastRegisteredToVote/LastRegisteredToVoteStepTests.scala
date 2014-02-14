package uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote

import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.mock.MockitoSugar
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model.{DateLeftSpecial, DateLeft, LastRegisteredType}

class LastRegisteredToVoteStepTests
  extends FlatSpec
  with Matchers
  with TestHelpers {
  
  behavior of "LastRegisteredToVoteStep.get"

  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/last-registered-to-vote").withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
          "<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/previously-registered"
      )
      contentAsString(result) should include("How were you last registered to vote?")
      contentAsString(result) should include("/register-to-vote/overseas/last-registered-to-vote")
    }
  }

  behavior of "LastRegisteredToVoteStep.post"

  it should "display errors on bad values" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "foo"
          )
      )

      status(result) should be(OK)
      contentAsString(result) should include("foo is not a valid registration type")
    }
  }

  it should "display errors on no values" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
    }
  }
  it should "should redirect to date last special step (army)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "army"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-army"))
    }
  }

  it should "should redirect to date last special step (council)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "council"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-council"))
    }
  }


  it should "should redirect to date last special step (crown)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "crown"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-crown"))
    }
  }

  it should "should redirect to date left uk step (uk)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "uk"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-uk"))
    }
  }

  it should "should redirect to date left uk step (not-registered)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "not-registered"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-uk"))
    }
  }

  it should "redirect to confirmation with a complete application" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withApplication(completeOverseasApplication)
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "uk"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/confirmation"))
    }
  }

  it should "redirect to date left special with different answer (uk -> army)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withApplication(
            completeOverseasApplication.copy(
              dateLeftSpecial = Some(DateLeftSpecial(DateLeft(1990, 12), LastRegisteredType.UK))
            )
          )
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "army"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-army"))
    }
  }

  it should "redirect to date left special with different answer (uk -> crown)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withApplication(
            completeOverseasApplication.copy(
              dateLeftSpecial = Some(DateLeftSpecial(DateLeft(1990, 12), LastRegisteredType.UK))
            )
          )
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "crown"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-crown"))
    }
  }

  it should "redirect to date left special with different answer (uk -> council)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withApplication(
            completeOverseasApplication.copy(
              dateLeftSpecial = Some(DateLeftSpecial(DateLeft(1990, 12), LastRegisteredType.UK))
            )
          )
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "council"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-council"))
    }
  }

  it should "redirect to date left special with different answer (army -> uk)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/last-registered-to-vote")
          .withIerSession()
          .withApplication(
            completeOverseasApplication.copy(
              dateLeftSpecial = Some(DateLeftSpecial(
                  DateLeft(1990, 12),
                  LastRegisteredType.Army)),
              dateLeftUk = None
            )
          )
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "uk"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-uk"))
    }
  }
  behavior of "LastRegisteredToVoteStep.editGet"

  it should "display the page" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(GET, "/register-to-vote/overseas/edit/last-registered-to-vote")
          .withIerSession()
      )

      status(result) should be(OK)
      contentType(result) should be(Some("text/html"))
      contentAsString(result) should include(
        "<a class=\"back-to-previous\" href=\"/register-to-vote/overseas/confirmation"
      )
      contentAsString(result) should include("How were you last registered to vote?")
      contentAsString(result) should include(
        "/register-to-vote/overseas/edit/last-registered-to-vote"
      )
    }
  }

  behavior of "LastRegisteredToVoteStep.editPost"

  it should "display errors on bad values" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "foo"
          )
      )

      status(result) should be(OK)
      contentAsString(result) should include("foo is not a valid registration type")
    }
  }

  it should "display errors on no values" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-registered-to-vote")
          .withIerSession()
      )

      status(result) should be(OK)
      contentAsString(result) should include("Please answer this question")
    }
  }
  it should "should redirect to date last special step (army)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "army"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-army"))
    }
  }

  it should "should redirect to date last special step (council)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "council"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-council"))
    }
  }


  it should "should redirect to date last special step (crown)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "crown"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-crown"))
    }
  }

  it should "should redirect to date left uk step (uk)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "uk"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-uk"))
    }
  }

  it should "should redirect to date left uk step (not-registered)" in {
    running(FakeApplication()) {
      val Some(result) = route(
        FakeRequest(POST, "/register-to-vote/overseas/edit/last-registered-to-vote")
          .withIerSession()
          .withFormUrlEncodedBody(
            "lastRegisteredToVote.registeredType" -> "not-registered"
          )
      )

      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-left-uk"))
    }
  }



}
