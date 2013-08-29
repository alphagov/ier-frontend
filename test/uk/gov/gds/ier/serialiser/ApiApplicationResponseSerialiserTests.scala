package uk.gov.gds.ier.serialiser

import org.specs2.mutable.Specification
import uk.gov.gds.ier.model.{ApiApplication, ApiApplicationResponse, WebApplication}
import org.joda.time.{DateTime, LocalDate}

class ApiApplicationResponseSerialiserTests extends Specification {

  val serialiser = new JsonSerialiser

  "ApiApplicationResponseSerialiser" should {
    "be able to serialise an instance of ApiApplicationResponse" in {
      val citizen = WebApplication(firstName = "John", lastName = "Smith",
        middleName = "James", previousLastName = "Jones", dob = LocalDate.now.withYear(1988)
          .withDayOfMonth(1)
          .withMonthOfYear(1), nino = "AB 12 34 56 D")
      val apiApplicationResponse = ApiApplicationResponse(detail = ApiApplication(citizen),
        ierId = "1234",
        createdAt = "1988-01-01 12:00:00",
        status = "unprocessed",
        source = "web"
      )

      val json = serialiser.toJson(apiApplicationResponse)
      json must contain(""""fn":"John"""")
      json must contain(""""mn":"James"""")
      json must contain(""""ln":"Smith"""")
      json must contain(""""pln":"Jones""")
      json must contain(""""dob":"1988-01-01"""")
      json must contain(""""nino":"AB 12 34 56 D"""")
      json must contain(""""status":"unprocessed"""")
      json must contain(""""source":"web"""")
      json must contain(""""ierId":"1234"""")
      json must contain(""""createdAt":"1988-01-01 12:00:00"""")
      json must contain(""""detail":{""")
    }

    "be able to deserialise a string" in {
      val json = """
        {
          "detail" : {
            "fn" : "John",
            "mn" : "James",
            "ln" : "Smith",
            "pln" : "Jones",
            "dob" : "1988-01-01",
            "nino" : "AB 12 34 56 D",
            "gssCode" : "test-gss-code"},
          "ierId" : "1234",
          "createdAt" : "1988-01-01 12:00:00",
          "status" : "Unprocessed",
          "source" : "web"
        }
                 """

      val apiApplicationResponse = serialiser.fromJson[ApiApplicationResponse](json)
      apiApplicationResponse.detail.fn mustEqual "John"
      apiApplicationResponse.detail.ln mustEqual "Smith"
      apiApplicationResponse.detail.mn mustEqual "James"
      apiApplicationResponse.detail.pln mustEqual "Jones"
      apiApplicationResponse.detail.dob mustEqual LocalDate.now().withYear(1988).withMonthOfYear(1).withDayOfMonth(1)
      apiApplicationResponse.detail.nino mustEqual "AB 12 34 56 D"
      apiApplicationResponse.detail.gssCode mustEqual "test-gss-code"
      apiApplicationResponse.ierId mustEqual "1234"
      apiApplicationResponse.createdAt mustEqual "1988-01-01 12:00:00"
      apiApplicationResponse.status mustEqual "Unprocessed"
      apiApplicationResponse.source mustEqual "web"
    }
  }

}
