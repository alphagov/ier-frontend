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

case class ContactUnderlying (contactType: String,
                              contactDetail: String)

object Contact {
  def apply (contactType:String,
             post: Option[String],
             phone: Option[String],
             textNum: Option[String],
             email: Option[String]) = {
    (contactType, post, phone, textNum, email) match {
      case ("phone", _, Some(p), _, _) => ContactUnderlying(contactType, p)
      case ("text",  _, _, Some(t), _) => ContactUnderlying(contactType, t)
      case ("email", _, _, _, Some(e)) => ContactUnderlying(contactType, e)
      case ("post",  Some(p), _, _, _) => ContactUnderlying(contactType, p)
    }
  }
  def unapply(contact:ContactUnderlying) : Option[(String, Option[String], Option[String], Option[String], Option[String])]= {
    contact match {
      case ContactUnderlying("phone", phone) => Some("phone", None, Some(phone), None, None)
      case ContactUnderlying("text", text) => Some("text", None, None, Some(text), None)
      case ContactUnderlying("email", email) => Some("email", None, None, None, Some(email))
      case ContactUnderlying("post", post) => Some("post", Some(post), None, None, None)
    }
  }
}

case class NameUnderlying(firstName:String,
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

case class Nationality (nationalities:Option[List[String]],
                        hasOtherCountries:Option[String],
                        otherCountries:Option[List[String]],
                        noNationalityReason:Option[String])

case class InprogressApplication (name: Option[NameUnderlying] = None,
                                  previousName: Option[NameUnderlying] = None,
                                  dobYear: Option[String] = None,
                                  dobMonth: Option[String] = None,
                                  dobDay: Option[String] = None,
                                  nationality: Option[Nationality] = None,
                                  nino: Option[String] = None,
                                  address: Option[String] = None,
                                  postcode: Option[String] = None,
                                  movedRecently: Option[String] = None,
                                  previousAddress: Option[String] = None,
                                  previousPostcode: Option[String] = None,
                                  hasOtherAddress: Option[String] = None,
                                  openRegisterOptin: Option[String] = None,
                                  contact: Option[ContactUnderlying] = None)

case class Address(addressLine:String, postcode:String)

case class PostcodeAnywhereResponse(Items:List[Map[String,String]])