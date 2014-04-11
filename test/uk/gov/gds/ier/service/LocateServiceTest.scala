package uk.gov.gds.ier.service

import uk.gov.gds.ier.client.{LocateApiClient, ApiClient}
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse, Address}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.WithConfig
import org.scalatest.{Matchers, FlatSpec}

class LocateServiceTest extends FlatSpec with Matchers {

  class MockConfig extends Config {
    override def locateUrl = "http://locate/addresses"
    override def locateApiAuthorizationToken = "abc"
  }

  behavior of "LocateService.lookupAddress"
  it should "be able to parse a response from PostcodeAnywhere" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, headers: (String, String)*) : ApiResponse = {
        if (url == "http://locate/addresses?residentialOnly=false&postcode=ab123cd") {
          Success("""[
            {
              "property": "1A Fake Flat",
              "street": "Fake House",
              "area": "123 Fake Street",
              "town": "Fakerton",
              "locality": "Fakesbury",
              "uprn": 12345678,
              "postcode": "AB12 3CD"
            }
          ]""", 0)
        } else {
          Fail("Bad postcode", 200)
        }
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    val addresses = service.lookupAddress("AB123CD")

    addresses.size should be(1)
    addresses(0).lineOne should be(Some("1A Fake Flat"))
    addresses(0).lineTwo should be(Some("Fake House"))
    addresses(0).lineThree should be(Some("Fakesbury"))
    addresses(0).city should be(Some("Fakerton"))
    addresses(0).county should be(Some("123 Fake Street"))
    addresses(0).uprn should be(Some("12345678"))
    addresses(0).postcode should be("AB12 3CD")
  }

  behavior of "LocateService.beaconFire"
  it should "return true if locate api is up" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, header: (String, String)*) : ApiResponse = {
        if (url.contains("status")) {
          Success("""{ "status" : "up" }""", 200)
        } else Fail("I'm not really locate", 200)
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(true)
  }
  it should "return false if locate api is down" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, header: (String, String)*) : ApiResponse = {
        if (url.contains("status")) {
          Success("""{ "status" : "down" }""", 200)
        } else Fail("I'm not really locate", 200)
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
  it should "return true if locate api doesn't respond" in {
    class FakeApiClient extends LocateApiClient(new MockConfig) {
      override def get(url: String, header: (String, String)*) : ApiResponse = {
        Fail("I'm not really locate", 200)
      }
    }
    val service = new LocateService(new FakeApiClient, new JsonSerialiser, new MockConfig)
    service.beaconFire should be(false)
  }
}