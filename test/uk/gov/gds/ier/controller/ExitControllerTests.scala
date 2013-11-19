package uk.gov.gds.ier.controller

import play.api.test._
import play.api.test.Helpers._
import org.scalatest.{Matchers, FlatSpec}

class ExitControllerTests extends FlatSpec with Matchers {

  "ExitController.scotland" should "display the scotland exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/scotland"))
      status(result) should be(OK)
      contentAsString(result) should include("Electoral registration form for Scotland")
    }
  }

  "ExitController.northernIreland" should "display the northern ireland exit page" in {
    running(FakeApplication()) {
      val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/northern-ireland"))
      status(result) should be(OK)
      contentAsString(result) should include("Electoral registration form for Northern Ireland")
    }
  }
}
