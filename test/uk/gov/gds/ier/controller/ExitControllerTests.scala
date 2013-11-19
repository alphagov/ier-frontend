package uk.gov.gds.ier.controller

import play.api.test._
import play.api.test.Helpers._
import org.scalatest.{Matchers, FlatSpec}

class ExitControllerTests extends FlatSpec with Matchers {

  running(FakeApplication()) {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/scotland"))

    "ExitController.scotland" should "display the scotland exit page" in {
      status(result) should be(OK)
      contentAsString(result) should include("Electoral registration form for Scotland")

      cookies(result).get("sessionKey").isDefined should be(true)
      cookies(result).get("sessionKey").get.maxAge.get should be < 0
      cookies(result).get("application").isDefined should be(true)
      cookies(result).get("application").get.maxAge.get should be < 0
    }
    it should "clear cookies correctly" in {
      cookies(result).get("sessionKey").isDefined should be(true)
      cookies(result).get("sessionKey").get.maxAge.get should be < 0
      cookies(result).get("application").isDefined should be(true)
      cookies(result).get("application").get.maxAge.get should be < 0
    }
  }

  running(FakeApplication()) {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/northern-ireland"))

    "ExitController.northernIreland" should "display the northern ireland exit page" in {
      status(result) should be(OK)
      contentAsString(result) should include("Electoral registration form for Northern Ireland")
    }
    it should "clear cookies correctly" in {
      cookies(result).get("sessionKey").isDefined should be(true)
      cookies(result).get("sessionKey").get.maxAge.get should be < 0
      cookies(result).get("application").isDefined should be(true)
      cookies(result).get("application").get.maxAge.get should be < 0
    }
  }
}
