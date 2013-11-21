package uk.gov.gds.ier.model

import org.joda.time.{DateTime, LocalDate}
import com.fasterxml.jackson.annotation.JsonFormat

case class ApiApplicationResponse (id: String,
                                   createdAt: String,
                                   status: String,
                                   source: String,
                                   gssCode: String)

case class Contact (contactMethod:String,
                    post: Option[String],
                    phone: Option[String],
                    textNum: Option[String],
                    email: Option[String]) {
  def toApiMap = {
    post.map(s => Map("post" -> s)).getOrElse(Map.empty) ++
      phone.map(s => Map("phone" -> s)).getOrElse(Map.empty) ++
      textNum.map(s => Map("text" -> s)).getOrElse(Map.empty) ++
      email.map(s => Map("email" -> s)).getOrElse(Map.empty)
  }
}

case class PreviousName(hasPreviousName:Boolean,
                        previousName:Option[Name]) {
  def toApiMap:Map[String,String] = {
    Map() ++ previousName.map(pn => pn.toApiMap("pfn", "pmn", "pln")).getOrElse(Map.empty)
  }
}

case class Name(firstName:String,
                middleNames:Option[String],
                lastName:String) {
  def toApiMap(fnKey:String, mnKey:String, lnKey:String):Map[String,String] = {
    Map(fnKey -> firstName, lnKey -> lastName) ++ middleNames.map(mn => Map(mnKey -> mn)).getOrElse(Map.empty)
  }
}

case class Nationality (british:Option[Boolean] = None,
                        irish:Option[Boolean] = None,
                        hasOtherCountry:Option[Boolean] = None,
                        otherCountries:List[String] = List.empty,
                        noNationalityReason:Option[String] = None,
                        countryIsos:Option[List[String]] = None) {
  def checkedNationalities = british.toList.filter(_ == true).map(brit => "British") ++
    irish.toList.filter(_ == true).map(isIrish => "Irish")

  def toApiMap = {
    val natMap = countryIsos.map(isos => Map("nat" -> isos.mkString(", "))).getOrElse(Map.empty)
    val noNatMap = noNationalityReason.map(nat => Map("nonat" -> nat)).getOrElse(Map.empty)
    natMap ++ noNatMap
  }
}

case class Nino(nino:Option[String],
                noNinoReason:Option[String]) {
  def toApiMap = {
    nino.map(n => Map("nino" -> n)).getOrElse(Map.empty) ++
    noNinoReason.map(nonino => Map("nonino" -> nonino)).getOrElse(Map.empty)
  }
}

case class DateOfBirth (year:Int,
                        month:Int,
                        day:Int) {
  def toApiMap = {
    Map("dob" -> (day + "/" + month + "/" + year))
  }
}

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
                                  contact: Option[Contact] = None,
                                  possibleAddresses: Option[PossibleAddress] = None) {
  def toApiMap:Map[String, String] = {
    Map.empty ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      nationality.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap("cadr", "cpost")).getOrElse(Map.empty) ++
      previousAddress.map(_.toApiMap).getOrElse(Map.empty) ++
      otherAddress.map(_.toApiMap).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalVoteOptin.map(postal => Map("pvote" -> postal.toString)).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class PossibleAddress(addresses:List[Address], postcode: String)

case class Addresses(addresses:List[Address])

case class Address(addressLine:Option[String], postcode:String) {
  def toApiMap(addressKey:String, postcodeKey:String) = {
    addressLine.map(address => Map(addressKey -> address)).getOrElse(Map.empty) ++ Map(postcodeKey -> postcode)
  }
}

case class PreviousAddress (movedRecently:Boolean,
                            previousAddress:Option[Address]) {
  def toApiMap = {
    previousAddress.map(_.toApiMap("padr", "ppost")).getOrElse(Map.empty)
  }
}

case class OtherAddress (hasOtherAddress:Boolean) {
  def toApiMap = {
    Map("oadr" -> hasOtherAddress.toString)
  }
}

case class PostcodeAnywhereResponse(Items:List[Map[String,String]])

case class ApiApplication(application:Map[String,String])
