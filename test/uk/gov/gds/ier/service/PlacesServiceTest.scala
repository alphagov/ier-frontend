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

  behavior of "PlacesService.lookupAddress"
  it should "be able to parse a response from PostcodeAnywhere" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String) : (ApiResponse, Long) = {
        if (url == "http://places/address?postcode=ab123cd") {
          (Success("""[
            {
              "lineOne": "1A Fake Flat",
              "lineTwo": "Fake House",
              "lineThree": "123 Fake Street",
              "lineFour": "",
              "lineFive": "",
              "city": "Fakerton",
              "county": "Fakesbury",
              "uprn": 12345678,
              "postcode": "AB12 3CD"
            }
          ]"""), 0)
        } else {
          (Fail("Bad postcode"), 0)
        }
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    val addresses = service.lookupAddress("AB123CD")

    addresses.size should be(1)
    addresses(0).lineOne should be(Some("1A Fake Flat"))
    addresses(0).lineTwo should be(Some("Fake House"))
    addresses(0).lineThree should be(Some("123 Fake Street"))
    addresses(0).city should be(Some("Fakerton"))
    addresses(0).county should be(Some("Fakesbury"))
    addresses(0).uprn should be(Some("12345678"))
    addresses(0).postcode should be("AB12 3CD")
  }

  behavior of "PlacesService.beaconFire"
  it should "return true if places is up" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String) : (ApiResponse, Long) = {
        if (url.contains("status")) {
          (Success("""{ "status" : "up" }"""), 0)
        } else (Fail("I'm not really places"), 0)
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(true)
  }
  it should "return false if places is down" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String) : (ApiResponse, Long) = {
        if (url.contains("status")) {
          (Success("""{ "status" : "down" }"""), 0)
        } else (Fail("I'm not really places"), 0)
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
  it should "return true if places doesn't respond" in {
    class FakeApiClient extends PlacesApiClient(new MockConfig) {
      override def get(url: String) : (ApiResponse, Long) = {
        (Fail("I'm not really places"), 0)
      }
    }
    val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
}
