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

case class ApiApplication(application:CompleteApplication)

case class CompleteApplication ( fn:String, mn:String, ln:String,
                                 pfn:String, pmn:String, pln:String,
                                 dob:String,
                                 nat:String, nonat:String,
                                 nino:String, nonino:String,
                                 cadr:String, cpost:String,
                                 padr:String, ppost:String,
                                 oadr:String,
                                 opnreg:String,
                                 post:String, phone:String, text:String, email:String,
                                 gssCode:String)

object CompleteApplication {
  def apply(inprogress:InprogressApplication):CompleteApplication = {
    CompleteApplication(
      fn = inprogress.name.map(_.firstName).getOrElse(""), mn = inprogress.name.map(_.middleNames).getOrElse(""), ln = inprogress.name.map(_.lastName).getOrElse(""),
      pfn = inprogress.previousName.map(_.previousName.map(_.firstName).getOrElse("")).getOrElse(""),
      pmn = inprogress.previousName.map(_.previousName.map(_.middleNames).getOrElse("")).getOrElse(""),
      pln = inprogress.previousName.map(_.previousName.map(_.lastName).getOrElse("")).getOrElse(""),
      dob = inprogress.dob.map(dob => dob.day + "/" + dob.month + "/" + dob.year).getOrElse(""),
      nat = inprogress.nationality.map(n => (n.nationalities ++ n.otherCountries).mkString(", ")).getOrElse(""),
      nonat = inprogress.nationality.map(_.noNationalityReason.getOrElse("")).getOrElse(""),
      nino = inprogress.nino.map(_.nino.getOrElse("")).getOrElse(""),
      nonino = inprogress.nino.map(_.noNinoReason.getOrElse("")).getOrElse(""),
      cadr = inprogress.address.map(_.addressLine).getOrElse(""),
      cpost = inprogress.address.map(_.postcode).getOrElse(""),
      padr = inprogress.previousAddress.map(_.previousAddress.map(_.addressLine).getOrElse("")).getOrElse(""),
      ppost = inprogress.previousAddress.map(_.previousAddress.map(_.postcode).getOrElse("")).getOrElse(""),
      oadr = inprogress.otherAddress.map(_.hasOtherAddress.toString).getOrElse("false"),
      opnreg = inprogress.openRegisterOptin.map(_.toString).getOrElse("false"),
      post = inprogress.contact.map(_.post.getOrElse("")).getOrElse(""),
      phone = inprogress.contact.map(_.phone.getOrElse("")).getOrElse(""),
      text = inprogress.contact.map(_.textNum.getOrElse("")).getOrElse(""),
      email = inprogress.contact.map(_.email.getOrElse("")).getOrElse(""),
      gssCode = ""
    )
  }
}