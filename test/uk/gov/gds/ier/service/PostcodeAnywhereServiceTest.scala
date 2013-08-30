package uk.gov.gds.ier.service

import org.specs2.mutable.Specification
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.model.{Fail, Success, ApiResponse}
import uk.gov.gds.ier.serialiser.JsonSerialiser

class PostcodeAnywhereServiceTest extends Specification {

  class FakeApiClient extends ApiClient {
    override def get(url: String, headers: (String, String)*) : ApiResponse = {
      Success("""{
        "Items": [
          {
            "Udprn": 51088262,
            "Company": "",
            "Department": "",
            "Line1": "Apartment 3/1",
            "Line2": "Block A",
            "Line3": "181 Sandy Row",
            "Line4": "",
            "Line5": "",
            "PostTown": "Belfast",
            "County": "County Antrim",
            "Postcode": "BT12 5EG",
            "Mailsort": 10121,
            "Barcode": "(BT125EG1B2)",
            "Type": "Residential",
            "DeliveryPointSuffix": "1B",
            "SubBuilding": "Apartment 3/1",
            "BuildingName": "Block A",
            "BuildingNumber": "181",
            "PrimaryStreet": "Sandy Row",
            "SecondaryStreet": "",
            "DoubleDependentLocality": "",
            "DependentLocality": "",
            "PoBox": "",
            "PrimaryStreetName": "Sandy",
            "PrimaryStreetType": "Row",
            "SecondaryStreetName": "",
            "SecondaryStreetType": "",
            "CountryName": "Northern Ireland"
          }]
        }""")
    }
  }

  "PostcodeAnywhereService" should {
    "be able to parse a response from PostcodeAnywhere" in {
      val service = new PostcodeAnywhereService(new FakeApiClient, new JsonSerialiser)
      val addresses = service.lookup("BT125EG")

      addresses.size mustEqual 1
      addresses(0).addressLine mustEqual "Apartment 3/1, Block A, 181 Sandy Row, Belfast, County Antrim"
      addresses(0).postcode mustEqual "BT12 5EG"
    }
  }

}
