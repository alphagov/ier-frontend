package uk.gov.gds.ier.controller

import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.DynamicGlobal
import play.api.test._
import play.api.test.Helpers._
import uk.gov.gds.ier.client.PlacesApiClient
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse}
import com.google.inject.Inject
import org.junit.runner.RunWith
import org.scalatest.{Matchers, FlatSpec}
import org.scalatest.junit.JUnitRunner
import uk.gov.gds.ier.service.PlacesService
import uk.gov.gds.ier.serialiser.JsonSerialiser

// FIXME: remove dead code
class StatusControllerTests extends FlatSpec with Matchers {

  val stubGlobal = new DynamicGlobal {
    override def bindings = { binder =>
      binder bind classOf[Config] to classOf[MockConfig]
//      binder bind classOf[PlacesService] to classOf[MockPlacesService]
    }
  }

  behavior of "StatusController.status"

  running(FakeApplication(withGlobal = Some(stubGlobal))) {
    val Some(result) = route(FakeRequest(GET, "/status"))
    it should "200 OK" in {
      status(result) should be(OK)
      contentType(result) should be(Some("application/json"))
    }
    it should "return a git sha" in {
      contentAsString(result) should include("\"revision\":\"a1b2c3d54\"")
    }
    it should "return a build date" in {
      contentAsString(result) should include("\"build date\":\"12/01/2012\"")
    }
    it should "return a build number" in {
      contentAsString(result) should include("\"build number\":\"0001\"")
    }
    it should "return a branch" in {
      contentAsString(result) should include("\"branch\":\"master\"")
    }
    it should "return an uptime" in {
      contentAsString(result) should fullyMatch regex ".*\"uptime\":\"\\d+:\\d+\".*".r
    }
    it should "return a process id" in {
      contentAsString(result) should fullyMatch regex ".*\"process id\":\"\\d+\".*".r
    }
    it should "return a started date and time" in {
      contentAsString(result) should fullyMatch regex ".*\"started\":\".+ \\d+:\\d+:\\d+ \\d+\".*".r
    }
    it should "claim to be up" in {
      contentAsString(result) should fullyMatch regex ".*\"status\":\"up\".*".r
    }
//    it should "have access to places" in {
//      contentAsString(result) should fullyMatch regex ".*\"places\":\"up\".*".r
//    }
  }
}

//class MockPlacesService @Inject() (apiClient: PlacesApiClient, serialiser: JsonSerialiser, config:Config) extends PlacesService(apiClient, serialiser, config) {
//  override def beaconFire = true
//}

class MockConfig extends Config {
  override def revision = "a1b2c3d54"
  override def buildDate = "12/01/2012"
  override def buildNumber = "0001"
  override def branch = "master"

  override def sessionTimeout = 20
  override def cookiesAesKey = "J1gs7djvi9/ecFHj0gNRbHHWIreobplsWmXnZiM2reo="
}
