package uk.gov.gds.ier.controller

import play.api.test._
import play.api.test.Helpers._
import org.scalatest.{Matchers, FlatSpec}

class RegisterToVoteControllerTests extends FlatSpec with Matchers {
  
  behavior of "RegisterToVoteController.registerToVote"

  it should "redirect to register-to-vote/country-of-residence" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest(GET, "/register-to-vote"))
      status(result) should be(SEE_OTHER)
      redirectLocation(result) should be(Some("/register-to-vote/country-of-residence")) 
    }
  }
}
