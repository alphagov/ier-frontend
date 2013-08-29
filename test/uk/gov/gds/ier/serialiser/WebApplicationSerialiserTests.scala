package uk.gov.gds.ier.serialiser

import org.specs2.mutable.Specification
import uk.gov.gds.ier.model.WebApplication
import org.joda.time.LocalDate


class WebApplicationSerialiserTests extends Specification {

  "WebApplicationSerialiser" should {
    val serialiser = new JsonSerialiser

    "serialise a WebApplication" in {
      val citizen = WebApplication(firstName = "John", middleName = "James",
        lastName = "Smith", previousLastName = "", nino = "AB 12 34 56 D" ,dob = LocalDate.now.withYear(1988).withMonthOfYear(1).withDayOfMonth(1))

      val jsonStr = serialiser.toJson(citizen)
      jsonStr must contain(""""firstName":"John"""")
      jsonStr must contain(""""middleName":"James"""")
      jsonStr must contain(""""lastName":"Smith"""")
      jsonStr must contain(""""dob":"1988-01-01"""")
      jsonStr must contain(""""nino":"AB 12 34 56 D"""")
    }

    "deserialise a WebApplication" in {
      val jsonStr = """
        {
          "firstName" : "John",
          "middleName" : "James",
          "lastName" : "Smith",
          "previousLastName" : "Jones",
          "dob" : "1988-01-01",
          "nino" : "AB 12 34 56 D"
        }
                    """

      val citizen = serialiser.fromJson[WebApplication](jsonStr)
      citizen.firstName mustEqual "John"
      citizen.lastName mustEqual "Smith"
      citizen.middleName mustEqual "James"
      citizen.dob.getYear mustEqual 1988
      citizen.dob.getMonthOfYear mustEqual 1
      citizen.dob.getDayOfMonth mustEqual 1
      citizen.nino mustEqual "AB 12 34 56 D"
    }
  }
}
