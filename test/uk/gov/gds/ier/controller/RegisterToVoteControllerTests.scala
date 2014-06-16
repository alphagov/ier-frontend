package uk.gov.gds.ier.controller

import play.api.test._
import play.api.test.Helpers._
import org.scalatest.{Matchers, FlatSpec}

class RegisterToVoteControllerTests extends FlatSpec with Matchers {

  behavior of "RegisterToVoteController.registerToVoteStart"
  it should "redirect to register-to-vote/country-of-residence" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/start"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/country-of-residence"))
    }
  }

  it should "pass query string parameters to the next page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/start?_ga=1234.TEST.4321"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/country-of-residence?_ga=1234.TEST.4321"))
    }
  }

    behavior of "RegisterToVoteController.registerToVoteOverseasStart"
  it should "redirect to register-to-vote/country-of-residence" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote/overseas/start"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/overseas/date-of-birth"))
    }
  }

  behavior of "RegisterToVoteController.registerToVote"
  it should "display the Register to Vote start page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote"))
      contentAsString(result) should include("/register-to-vote/start")
    }
  }
}
