package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority

case class ApiApplicationResponse (id: String,
                                   createdAt: String,
                                   status: String,
                                   source: String,
                                   gssCode: String)

case class ContactDetail (contactMe:Boolean,
                          detail:Option[String])

case class Contact (post: Boolean,
                    phone: Option[ContactDetail],
                    email: Option[ContactDetail]) {
  def toApiMap = {
    Map("post" -> post.toString) ++
      phone.filter(_.contactMe).flatMap(_.detail).map("phone" -> _).toMap ++
      email.filter(_.contactMe).flatMap(_.detail).map("email" -> _).toMap
  }
}

case class Country (country: String)

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

case class IsoNationality(countryIsos:List[String] = List.empty,
                          noNationalityReason:Option[String] = None) { 
  def toApiMap = {
    val natMap = if (countryIsos.isEmpty) Map.empty else Map("nat" -> countryIsos.mkString(", "))
    val noNatMap = noNationalityReason.map(nat => Map("nonat" -> nat)).getOrElse(Map.empty)
    natMap ++ noNatMap
  }
}


case class PartialNationality (british:Option[Boolean] = None,
                               irish:Option[Boolean] = None,
                               hasOtherCountry:Option[Boolean] = None,
                               otherCountries:List[String] = List.empty,
                               noNationalityReason:Option[String] = None) {
  def checkedNationalities = british.toList.filter(_ == true).map(brit => "United Kingdom") ++
    irish.toList.filter(_ == true).map(isIrish => "Ireland")
}

case class Nino(nino:Option[String],
                noNinoReason:Option[String]) {
  def toApiMap = {
    nino.map(n => Map("nino" -> n)).getOrElse(Map.empty) ++
    noNinoReason.map(nonino => Map("nonino" -> nonino)).getOrElse(Map.empty)
  }
}

case class noDOB(reason:Option[String],
                 range:Option[String]) {
  def toApiMap = {
    reason.map(r => Map("nodobreason" -> r)).getOrElse(Map.empty) ++
    range.map(r => Map("agerange" -> r)).getOrElse(Map.empty)
  }
}

case class DOB(year:Int,
               month:Int,
               day:Int) {
  def toApiMap = {
    Map("dob" -> (year + "-" + "%02d".format(month) + "-" + "%02d".format(day)))
  }
}

case class DateOfBirth(dob:Option[DOB],
                       noDob:Option[noDOB]) {
  def toApiMap = {
    dob.map(_.toApiMap).getOrElse(Map.empty) ++
    noDob.map(_.toApiMap).getOrElse(Map.empty)
  }
}


trait InprogressApplication[T] {
  def merge(other: T):T
}

case class InprogressOrdinary (name: Option[Name] = None,
                                  previousName: Option[PreviousName] = None,
                                  dob: Option[DateOfBirth] = None,
                                  nationality: Option[PartialNationality] = None,
                                  nino: Option[Nino] = None,
                                  address: Option[PartialAddress] = None,
                                  previousAddress: Option[PartialPreviousAddress] = None,
                                  otherAddress: Option[OtherAddress] = None,
                                  openRegisterOptin: Option[Boolean] = None,
                                  postalVote: Option[PostalVote] = None,
                                  contact: Option[Contact] = None,
                                  possibleAddresses: Option[PossibleAddress] = None,
                                  country: Option[Country] = None) extends InprogressApplication[InprogressOrdinary] {

  def merge(other: InprogressOrdinary):InprogressOrdinary = {
    other.copy(
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      dob = this.dob.orElse(other.dob),
      nationality = this.nationality.orElse(other.nationality),
      nino = this.nino.orElse(other.nino),
      address = this.address.orElse(other.address),
      previousAddress = this.previousAddress.orElse(other.previousAddress),
      otherAddress = this.otherAddress.orElse(other.otherAddress),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      postalVote = this.postalVote.orElse(other.postalVote),
      contact = this.contact.orElse(other.contact),
      possibleAddresses = None,
      country = this.country.orElse(other.country)
    )
  }
}

trait CompleteApplication {
  def toApiMap:Map[String, String]
}

case class OrdinaryApplication(name: Option[Name],
                               previousName: Option[PreviousName],
                               dob: Option[DateOfBirth],
                               nationality: Option[IsoNationality],
                               nino: Option[Nino],
                               address: Option[Address],
                               previousAddress: Option[Address],
                               otherAddress: Option[OtherAddress],
                               openRegisterOptin: Option[Boolean],
                               postalVote: Option[PostalVote],
                               contact: Option[Contact],
                               referenceNumber: Option[String],
                               authority: Option[LocalAuthority],
                               previousAuthority: Option[LocalAuthority],
                               ip: Option[String]) extends CompleteApplication {
  def toApiMap:Map[String, String] = {
    Map.empty ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      nationality.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      previousAddress.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      otherAddress.map(_.toApiMap).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalVote.map(postalVote => postalVote.postalVoteOption.map(
        postalVoteOption => Map("pvote" -> postalVoteOption.toString)).getOrElse(Map.empty)).getOrElse(Map.empty) ++
      postalVote.map(postalVote => postalVote.deliveryMethod.map(
        deliveryMethod => deliveryMethod.emailAddress.map(
        emailAddress => Map("pvoteemail" -> emailAddress)).getOrElse(Map.empty)).getOrElse(Map.empty)).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty) ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      authority.map(auth => Map("gssCode" -> auth.gssId)).getOrElse(Map.empty)  ++
      previousAuthority.map(auth => Map("pgssCode" -> auth.gssId)).getOrElse(Map.empty) ++
      ip.map(ipAddress => Map("ip" -> ipAddress)).getOrElse(Map.empty) ++
      Map("applicationType" -> "ordinary")
  }
}

case class PostalVote (postalVoteOption: Option[Boolean], deliveryMethod: Option[PostalVoteDeliveryMethod])

case class PostalVoteDeliveryMethod(deliveryMethod: Option[String], emailAddress: Option[String])

case class PossibleAddress(jsonList:Addresses, postcode: String)

case class Addresses(addresses:List[PartialAddress])

case class PartialAddress(addressLine:Option[String], 
                          uprn:Option[String], 
                          postcode:String, 
                          manualAddress:Option[String])

case class Address(lineOne:Option[String], 
                   lineTwo:Option[String],
                   lineThree:Option[String],
                   city:Option[String],
                   county:Option[String],
                   uprn:Option[String],
                   postcode:String) {
  def prettyAddressLine = {
    val addressLine = lineOne ++: lineTwo ++: lineThree ++: List(postcode)
    addressLine.mkString(", ")
  }

  def toApiMap(addressKey:String) = {
    lineOne.map(x => Map(addressKey + "property" -> x)).getOrElse(Map.empty) ++
      lineTwo.map(x => Map(addressKey + "street" -> x)).getOrElse(Map.empty) ++
      lineThree.map(x => Map(addressKey + "locality" -> x)).getOrElse(Map.empty) ++
      city.map(x => Map(addressKey + "town" -> x)).getOrElse(Map.empty) ++
      county.map(x => Map(addressKey + "area" -> x)).getOrElse(Map.empty) ++
      uprn.map(x => Map(addressKey + "uprn" -> x)).getOrElse(Map.empty) ++
      Map(addressKey + "postcode" -> postcode)
  }
}

case class PartialPreviousAddress (movedRecently:Option[Boolean],
                                   findAddress:Boolean,
                                   previousAddress:Option[PartialAddress])

case class OtherAddress (otherAddressOption:OtherAddressOption) {
  def toApiMap = {
    Map("oadr" -> otherAddressOption.hasOtherAddress.toString)
  }
}

case class OtherAddressOption(hasOtherAddress:Boolean, name:String)

object OtherAddress {
  val NoOtherAddress = OtherAddressOption(false, "none")

  val StudentOtherAddress = OtherAddressOption(true, "student")

  val HomeOtherAddress = OtherAddressOption(true, "secondHome")

  def parse(str:String):OtherAddressOption = {
    str match {
      case "secondHome" => HomeOtherAddress
      case "student" => StudentOtherAddress
      case _ => NoOtherAddress
    }
  }
}

case class PostcodeAnywhereResponse(Items:List[Map[String,String]])

case class ApiApplication(application:Map[String,String])
