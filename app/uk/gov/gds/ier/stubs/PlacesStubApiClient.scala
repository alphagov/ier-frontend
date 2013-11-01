package uk.gov.gds.ier.stubs

import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{Fail, ApiApplicationResponse, Success, ApiResponse}
import java.util.UUID
import org.joda.time.DateTime
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.client.PlacesApiClient

@Singleton
class PlacesStubApiClient @Inject() (config: Config, serialiser: JsonSerialiser) extends PlacesApiClient(config) {

  override def get(url: String) : ApiResponse = {
    if (url.startsWith(config.placesUrl + "/address?postcode=")) {
      Success("""[
            {
              "uprn": 10000001,
              "lineOne": "GDS",
              "lineTwo": "Aviation house",
              "lineThree": "125 Kingsway",
              "lineFour": "",
              "lineFive": "",
              "city": "London",
              "county": "Greater London",
              "postcode": "WC2B 6SE"
            }
          ]""")
    }
    else if (url.startsWith(config.placesUrl + "/authority?postcode=")) {
      Success("{\"name\":\"Camden Borough Council\",\"ero\":{\"telephoneNumber\":\"\"},\"opcsId\":\"00AG\",\"gssId\":\"E09000007\"}")
    }
    else {
      Fail("Bad postcode")
    }
  }
}
