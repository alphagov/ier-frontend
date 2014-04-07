package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.PlacesApiClient
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import org.scalatest.{Matchers, FlatSpec}

class PlacesServiceTest extends FlatSpec with Matchers {

  class MockConfig extends Config {
    override def placesUrl = "http://places"
  }

  behavior of "PlacesService.beaconFire"
  it should "return true if places is up" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String, headers:(String, String)*) : ApiResponse = {
        if (url.contains("status")) {
          Success("""{ "status" : "up" }""", 0)
        } else Fail("I'm not really places", 0)
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(true)
  }
  it should "return false if places is down" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String, headers:(String, String)*) : ApiResponse = {
        if (url.contains("status")) {
          Success("""{ "status" : "down" }""", 0)
        } else Fail("I'm not really places", 0)
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
  it should "return true if places doesn't respond" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String, headers:(String, String)*) : ApiResponse = {
        Fail("I'm not really places", 0)
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
}
