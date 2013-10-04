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

case class CompleteApplication (firstName: Option[String],
                                middleName: Option[String],
                                lastName: Option[String],
                                previousLastName: Option[String],
                                nino: Option[String],
                                dob: Option[LocalDate],
                                nationality: Option[String])

case class Contact (contactType:String,
                    post: Option[String],
                    phone: Option[String],
                    textNum: Option[String],
                    email: Option[String])

case class NameUnderlying(firstName:String,
                          middleNames:String,
                          lastName:String)

case class PreviousName(hasPreviousName:Boolean,
                        previousName:Option[NameUnderlying])

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

case class Nationality (nationalities:List[String],
                        otherCountries:List[String],
                        noNationalityReason:Option[String])

case class Nino(nino:Option[String],
                noNinoReason:Option[String])

case class DateOfBirth (year:String,
                        month:String,
                        day:String)

case class InprogressApplication (name: Option[NameUnderlying] = None,
                                  previousName: Option[PreviousName] = None,
                                  dob: Option[DateOfBirth] = None,
                                  nationality: Option[Nationality] = None,
                                  nino: Option[Nino] = None,
                                  address: Option[Address] = None,
                                  previousAddress: Option[PreviousAddress] = None,
                                  otherAddress: Option[OtherAddress] = None,
                                  openRegisterOptin: Option[Boolean] = None,
                                  contact: Option[Contact] = None)

case class Address(addressLine:String, postcode:String)

case class PreviousAddress (movedRecently:Boolean,
                            previousAddress:Option[Address])

case class OtherAddress (hasOtherAddress:Boolean)

case class PostcodeAnywhereResponse(Items:List[Map[String,String]])