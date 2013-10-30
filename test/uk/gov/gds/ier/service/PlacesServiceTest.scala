package uk.gov.gds.ier.service

import org.specs2.mutable.Specification
import uk.gov.gds.ier.client.{PlacesApiClient, ApiClient}
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.guice.WithConfig

class PlacesServiceTest extends Specification {

  class MockConfig extends Config {
    override def placesUrl = "http://places"
  }

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

  "PostcodeAnywhereService" should {
    "be able to parse a response from PostcodeAnywhere" in {
      val service = new PlacesService(new FakeApiClient, new JsonSerialiser, new MockConfig)
      val addresses = service.lookupAddress("BT125EG")

      addresses.size mustEqual 1
      addresses(0).addressLine mustEqual "Apartment 3/1, Block A, 181 Sandy Row, Belfast, County Antrim"
      addresses(0).postcode mustEqual "BT12 5EG"
    }
  }
}
