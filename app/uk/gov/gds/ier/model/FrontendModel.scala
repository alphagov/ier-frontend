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

case class CompleteApplication(firstName: Option[String],
                               middleName: Option[String],
                               lastName: Option[String],
                               previousLastName: Option[String],
                               nino: Option[String],
                               dob: Option[LocalDate],
                               nationality: Option[String])

case class NameUnderlying( firstName:String,
                 middleNames:String,
                 lastName:String)

object Name {
  def apply(firstName:String,
            middleNames:Option[String],
            lastName:String) = {
    NameUnderlying(firstName, middleNames.getOrElse(""), lastName)
  }
  def unapply(name:NameUnderlying) = {
    Some((name.firstName, Option(name.middleNames), name.lastName))
  }
}

case class InprogressApplication( name: Option[NameUnderlying] = None,
                                  nameChange: Option[String] = None,
                                  previousFirstName: Option[String] = None,
                                  previousMiddleName: Option[String] = None,
                                  previousLastName: Option[String] = None,
                                  dobYear: Option[String] = None,
                                  dobMonth: Option[String] = None,
                                  dobDay: Option[String] = None,
                                  nationality: Option[String] = None,
                                  nino: Option[String] = None,
                                  address: Option[String] = None,
                                  postcode: Option[String] = None,
                                  movedRecently: Option[String] = None,
                                  previousAddress: Option[String] = None,
                                  previousPostcode: Option[String] = None,
                                  hasOtherAddress: Option[String] = None,
                                  otherAddress: Option[String] = None,
                                  otherPostcode: Option[String] = None)

case class Address(addressLine:String, postcode:String)

case class PostcodeAnywhereResponse(Items:List[Map[String,String]])