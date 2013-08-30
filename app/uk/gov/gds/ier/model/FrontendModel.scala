package uk.gov.gds.ier.model

import org.joda.time.{DateTime, LocalDate}
import com.fasterxml.jackson.annotation.JsonFormat

case class WebApplication (firstName:String,
                           middleName: String,
                           lastName:String,
                           previousLastName: String,
                           nino: String,
                           dob: LocalDate)

case class ApiApplication (fn:String,
                           mn: String,
                           ln:String,
                           pln:String,
                           gssCode: String = "badGSS",
                           nino: String,
                           dob: LocalDate)


case class ApiApplicationResponse (detail: ApiApplication,
                                   ierId: String,
                                   createdAt: String,
                                   status: String,
                                   source: String)

object ApiApplication {
  def apply(applicant:WebApplication): ApiApplication = {
    ApiApplication(
      fn = applicant.firstName,
      pln= applicant.previousLastName,
      ln= applicant.lastName,
      mn = applicant.middleName,
      nino = applicant.nino,
      dob = applicant.dob
    )
  }
}

case class Address(addressLine:String, postcode:String)

case class PostcodeAnywhereResponse(Items:List[Map[String,String]])