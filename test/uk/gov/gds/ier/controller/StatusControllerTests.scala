package uk.gov.gds.ier.controller

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import org.specs2.mutable.Specification
import org.specs2.matcher.Matchers
import play.api.test._
import play.api.test.Helpers._

class StatusControllerTests extends Specification with Matchers {

  val stubGlobal = new DynamicGlobal {
    override def bindings = { binder =>
        binder bind classOf[Config] to classOf[MockConfig]
    }
  }

  "StatusController.status" should {
    running(FakeApplication(withGlobal = Some(stubGlobal))) {
      val Some(result) = route(FakeRequest(GET, "/status"))
      "200 OK" in {
        status(result) must equalTo(OK)
        contentType(result) must beSome("application/json")
      }
      "return a git sha" in {
        contentAsString(result) must contain("\"revision\":\"a1b2c3d54\"")
      }
      "return a build date" in {
        contentAsString(result) must contain("\"build date\":\"12/01/2012\"")
      }
      "return a build number" in {
        contentAsString(result) must contain("\"build number\":\"0001\"")
      }
      "return a branch" in {
        contentAsString(result) must contain("\"branch\":\"master\"")
      }
      "return an uptime" in {
        contentAsString(result) must matching(".*\"uptime\":\"\\d+:\\d+\".*".r)
      }
      "return a process id" in {
        contentAsString(result) must matching(".*\"process id\":\"\\d+\".*".r)
      }
      "return a started date and time" in {
        contentAsString(result) must matching(".*\"started\":\"\\w+ \\d+ \\w+ \\d+:\\d+:\\d+ \\d+\".*".r)
      }
      "claim to be up" in {
        contentAsString(result) must matching(".*\"status\":\"up\".*".r)
      }
    }
  }
}

class MockConfig extends Config {
  override def revision = "a1b2c3d54"
  override def buildDate = "12/01/2012"
  override def buildNumber = "0001"
  override def branch = "master"
}