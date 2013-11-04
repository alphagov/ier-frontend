package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.{PlacesApiClient, ApiClient}
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.WithConfig
import org.scalatest.{Matchers, FlatSpec}

class PlacesServiceTest extends FlatSpec with Matchers {

  class MockConfig extends Config {
    override def placesUrl = "http://places"
  }

  behavior of "PlacesService.lookupAddress"
  it should "be able to parse a response from PostcodeAnywhere" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String) : ApiResponse = {
        if (url == "http://places/address?postcode=bt125eg") {
          Success("""[
            {
              "uprn": 51088262,
              "lineOne": "Apartment 3/1",
              "lineTwo": "Block A",
              "lineThree": "181 Sandy Row",
              "lineFour": "",
              "lineFive": "",
              "city": "Belfast",
              "county": "County Antrim",
              "postcode": "BT12 5EG"
            }
          ]""")
        } else {
          Fail("Bad postcode")
        }
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    val addresses = service.lookupAddress("BT125EG")

    addresses.size should be(1)
    addresses(0).addressLine should be(Some("Apartment 3/1, Block A, 181 Sandy Row, Belfast, County Antrim"))
    addresses(0).postcode should be("BT12 5EG")
  }

  behavior of "PlacesService.beaconFire"
  it should "return true if places is up" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String) : ApiResponse = {
        if (url.contains("status")) {
          Success("""{ "status" : "up" }""")
        } else Fail("I'm not really places")
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(true)
  }
  it should "return false if places is down" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String) : ApiResponse = {
        if (url.contains("status")) {
          Success("""{ "status" : "down" }""")
        } else Fail("I'm not really places")
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
  it should "return true if places doesn't respond" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String) : ApiResponse = {
        Fail("I'm not really places")
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
}
