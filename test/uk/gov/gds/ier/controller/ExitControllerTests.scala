package uk.gov.gds.ier.controller

import uk.gov.gds.ier.test.ControllerTestSuite

class ExitControllerTests extends ControllerTestSuite {

  "ExitController.scotland" should "display the scotland exit page" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/scotland"))
    status(result) should be(OK)
    contentAsString(result) should include("Voter registration forms for Scotland")

    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }

  it should "clear cookies correctly" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/scotland"))
    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }

  "ExitController.northernIreland" should "display the northern ireland exit page" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/northern-ireland"))
    status(result) should be(OK)
    contentAsString(result) should include("Voter canvass form for Northern Ireland")
  }

  it should "clear cookies correctly" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/northern-ireland"))
    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }

  "ExitController.tooYoungScotland" should "display the scotland under 14 exit page" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/too-young-scotland"))
    status(result) should be(OK)
    contentAsString(result) should include("You can’t register to vote until you’re 14 in Scotland")

    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }

  it should "clear cookies correctly" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/too-young-scotland"))
    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }

  "ExitController.tooYoungNotScotland14" should "display the young Non-Scotland 14yr old exit page" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/too-young-not-scotland-14"))
    status(result) should be(OK)
    contentAsString(result) should include("As a 14 year old you can only register from an address in Scotland")

    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }

  "ExitController.tooYoungNotScotland15" should "display the young Non-Scotland 15yr old exit page" in runningApp {
    val Some(result) = route(FakeRequest("GET", "/register-to-vote/exit/too-young-not-scotland-15"))
    status(result) should be(OK)
    contentAsString(result) should include("As a 15 year old you can only register from an address in Scotland")

    cookies(result).get("sessionKey").isDefined should be(true)
    cookies(result).get("sessionKey").get.maxAge.get should be < 0
    cookies(result).get("application").isDefined should be(true)
    cookies(result).get("application").get.maxAge.get should be < 0
  }
}
