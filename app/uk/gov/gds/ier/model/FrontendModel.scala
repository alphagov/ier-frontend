package uk.gov.gds.ier.model

import org.joda.time.{DateTime, LocalDate}
import com.fasterxml.jackson.annotation.JsonFormat

case class ApiApplicationResponse (id: String,
                                   createdAt: String,
                                   status: String,
                                   source: String,
                                   gssCode: String)

case class Contact (contactType:String,
                    post: Option[String],
                    phone: Option[String],
                    textNum: Option[String],
                    email: Option[String])

case class PreviousName(hasPreviousName:Boolean,
                        previousName:Option[Name])

case class Name(firstName:String,
                middleNames:Option[String],
                lastName:String)

case class Nationality (nationalities:List[String] = List.empty,
                        otherCountries:List[String] = List.empty,
                        noNationalityReason:Option[String] = None)

case class Nino(nino:Option[String],
                noNinoReason:Option[String])

case class DateOfBirth (year:Int,
                        month:Int,
                        day:Int)

case class InprogressApplication (name: Option[Name] = None,
                                  previousName: Option[PreviousName] = None,
                                  dob: Option[DateOfBirth] = None,
                                  nationality: Option[Nationality] = None,
                                  nino: Option[Nino] = None,
                                  address: Option[Address] = None,
                                  previousAddress: Option[PreviousAddress] = None,
                                  otherAddress: Option[OtherAddress] = None,
                                  openRegisterOptin: Option[Boolean] = None,
                                  postalVoteOptin: Option[Boolean] = None,
                                  contact: Option[Contact] = None)

case class Address(addressLine:String, postcode:String)

case class PreviousAddress (movedRecently:Boolean,
                            previousAddress:Option[Address])

case class OtherAddress (hasOtherAddress:Boolean)

case class PostcodeAnywhereResponse(Items:List[Map[String,String]])

case class ApiApplication(application:CompleteApplication)

case class CompleteApplication ( fn:Option[String], mn:Option[String], ln:Option[String],
                                 pfn:Option[String], pmn:Option[String], pln:Option[String],
                                 dob:Option[String],
                                 nat:Option[String], nonat:Option[String],
                                 nino:Option[String], nonino:Option[String],
                                 cadr:Option[String], cpost:Option[String],
                                 padr:Option[String], ppost:Option[String],
                                 oadr:Option[String],
                                 opnreg:Option[String],
                                 post:Option[String], phone:Option[String], text:Option[String], email:Option[String],
                                 gssCode:Option[String])

object CompleteApplication {
  def apply(inprogress:InprogressApplication):CompleteApplication = {
    CompleteApplication(
      fn = inprogress.name.map(_.firstName), mn = inprogress.name.map(_.middleNames).getOrElse(None), ln = inprogress.name.map(_.lastName),
      pfn = inprogress.previousName.map(_.previousName.map(_.firstName)).getOrElse(None),
      pmn = inprogress.previousName.map(_.previousName.map(_.middleNames).getOrElse(None)).getOrElse(None),
      pln = inprogress.previousName.map(_.previousName.map(_.lastName)).getOrElse(None),
      dob = inprogress.dob.map(dob => dob.day + "/" + dob.month + "/" + dob.year),
      nat = inprogress.nationality.map(n => (n.nationalities ++ n.otherCountries).mkString(", ")),
      nonat = inprogress.nationality.map(_.noNationalityReason).getOrElse(None),
      nino = inprogress.nino.map(_.nino).getOrElse(None),
      nonino = inprogress.nino.map(_.noNinoReason).getOrElse(None),
      cadr = inprogress.address.map(_.addressLine),
      cpost = inprogress.address.map(_.postcode),
      padr = inprogress.previousAddress.map(_.previousAddress.map(_.addressLine)).getOrElse(None),
      ppost = inprogress.previousAddress.map(_.previousAddress.map(_.postcode)).getOrElse(None),
      oadr = inprogress.otherAddress.map(_.hasOtherAddress.toString),
      opnreg = inprogress.openRegisterOptin.map(_.toString),
      post = inprogress.contact.map(_.post).getOrElse(None),
      phone = inprogress.contact.map(_.phone).getOrElse(None),
      text = inprogress.contact.map(_.textNum).getOrElse(None),
      email = inprogress.contact.map(_.email).getOrElse(None),
      gssCode = None
    )
  }
}
