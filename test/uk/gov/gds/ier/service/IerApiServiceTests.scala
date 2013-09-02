package uk.gov.gds.ier.service

import org.specs2.mutable.Specification
import uk.gov.gds.ier.client.ApiClient
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.serialiser.JsonSerialiser
import org.joda.time.{DateTime, LocalDate}
import uk.gov.gds.ier.model.WebApplication
import uk.gov.gds.ier.model.Success
import uk.gov.gds.ier.model.Fail
import scala.Some
import uk.gov.gds.ier.config.Config

class IerApiServiceTests extends Specification {

  class MockConfig extends Config

  class FakeApiClient extends ApiClient(new MockConfig) {
    override def post(url:String, content:String) : ApiResponse = {
      if (content contains """"fn":"John"""") Success("""{
          "ierId" : "1234",
          "createdAt" : "1988-01-01 12:00:00",
          "status" : "Unprocessed",
          "source" : "web",
          "detail" : """ + content + "}")
      else Fail("Invalid firstName in " + content)
    }
  }

  "IerApiService" should {
    val service = new IerApiService(new FakeApiClient, new JsonSerialiser)

    "can post to IerApi" in {
      val citizen = WebApplication(firstName = "John", lastName = "Smith",
        middleName = "James", previousLastName = "Jones", dob = LocalDate.now, nino = "AB 12 34 56 D")
      val citizenWithId = ApiApplicationResponse(detail = ApiApplication(citizen),
        ierId = "1234",
        createdAt = "1988-01-01 12:00:00",
        status = "Unprocessed",
        source = "web"
      )

      service.submitApplication(citizen) mustEqual citizenWithId
    }
  }
}
