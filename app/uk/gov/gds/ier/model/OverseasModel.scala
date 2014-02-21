package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority
import scala.util.Try

case class InprogressOverseas(
    overseasName: Option[OverseasName] = None,
    previouslyRegistered: Option[PreviouslyRegistered] = None,
    dateLeftSpecial: Option[DateLeftSpecial] = None,
    dateLeftUk: Option[DateLeft] = None,
    overseasParentName: Option[OverseasParentName] = None,
    parentsAddress: Option[Stub] = None,
    lastRegisteredToVote: Option[LastRegisteredToVote] = None,
    dob: Option[DOB] = None,
    nino: Option[Nino] = None,
    lastUkAddress: Option[PartialAddress] = None,
    address: Option[OverseasAddress] = None,
    openRegisterOptin: Option[Boolean] = None,
    waysToVote: Option[WaysToVote] = None,
    postalOrProxyVote: Option[PostalOrProxyVote] = None,
    contact: Option[Contact] = None,
    passport: Option[Passport] = None,
    possibleAddresses: Option[PossibleAddress] = None)
  extends InprogressApplication[InprogressOverseas] {

  def merge(other:InprogressOverseas) = {
    other.copy(
      overseasName = this.overseasName.orElse(other.overseasName),
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
      dateLeftSpecial = this.dateLeftSpecial.orElse(other.dateLeftSpecial),
      dateLeftUk = this.dateLeftUk.orElse(other.dateLeftUk),
      overseasParentName = this.overseasParentName.orElse(other.overseasParentName),
      lastRegisteredToVote = this.lastRegisteredToVote.orElse(other.lastRegisteredToVote),
      dob = this.dob.orElse(other.dob),
      nino = this.nino.orElse(other.nino),
      lastUkAddress = this.lastUkAddress.orElse(other.lastUkAddress),
      address = this.address.orElse(other.address),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      waysToVote = this.waysToVote.orElse(other.waysToVote),
      postalOrProxyVote = this.postalOrProxyVote.orElse(other.postalOrProxyVote),
      contact = this.contact.orElse(other.contact),
      passport = this.passport.orElse(other.passport),
      possibleAddresses = None
    )
  }
}

case class OverseasApplication(
    overseasName: Option[OverseasName],
    previouslyRegistered: Option[PreviouslyRegistered],
    dateLeftUk: Option[DateLeft],
    dateLeftSpecial: Option[DateLeftSpecial],
    overseasParentName: Option[OverseasParentName] = None,
    lastRegisteredToVote: Option[LastRegisteredToVote],
    dob: Option[DOB],
    nino: Option[Nino],
    address: Option[OverseasAddress],
    lastUkAddress: Option[Address],
    openRegisterOptin: Option[Boolean],
    postalOrProxyVote: Option[PostalOrProxyVote],
    passport: Option[Passport],
    contact: Option[Contact],
    referenceNumber: Option[String],
    authority: Option[LocalAuthority],
    ip: Option[String])
  extends CompleteApplication {

  def toApiMap = {
    Map.empty ++
      overseasName.flatMap(_.name.map(_.toApiMap("fn", "mn", "ln"))).getOrElse(Map.empty) ++
      overseasName.flatMap(_.previousName.map(_.toApiMap)).getOrElse(Map.empty) ++ 
      previouslyRegistered.map(_.toApiMap(lastRegisteredToVote)).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap()).getOrElse(Map.empty) ++
      dateLeftSpecial.map(_.toApiMap).getOrElse(Map.empty) ++
      overseasParentName.flatMap(_.name.map(_.toApiMap("pgfn", "pgmn", "pgln"))).getOrElse(Map.empty) ++
      overseasParentName.flatMap(_.previousName.map(_.toApiMap)).getOrElse(Map.empty) ++ 
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      lastUkAddress.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      dob.map(_.toApiMap("dob")).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty) ++
      lastUkAddress.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(_.toApiMap).getOrElse(Map.empty) ++
      passport.map(_.toApiMap).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty) ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      authority.map(auth => Map("gssCode" -> auth.gssId)).getOrElse(Map.empty) ++
      ip.map(ipAddress => Map("ip" -> ipAddress)).getOrElse(Map.empty) ++
      Map("applicationType" -> "overseas")
  }
}

case class PreviouslyRegistered(hasPreviouslyRegistered: Boolean) {
  def toApiMap(lastReg: Option[LastRegisteredToVote]) = {
    if (hasPreviouslyRegistered) Map("lastcategory" -> "overseas")
    else lastReg.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class DateLeftSpecial (date:DateLeft) {
  def toApiMap = {
    date.toApiMap("dcs")
  }
}

case class DateLeft (year:Int, month:Int) {
  def toApiMap(key:String = "leftuk") = {
    Map(key -> "%04d-%02d".format(year,month))
  }
}

case class LastRegisteredToVote (lastRegisteredType:LastRegisteredType) {
  def toApiMap = Map("lastcategory" -> lastRegisteredType.name)
}

sealed case class LastRegisteredType(name:String)

object LastRegisteredType {
  val Ordinary = LastRegisteredType("ordinary")
  val Forces = LastRegisteredType("forces")
  val Crown = LastRegisteredType("crown")
  val Council = LastRegisteredType("council")
  val NotRegistered = LastRegisteredType("not-registered")

  def isValid(str:String) = {
    Try {
      parse(str)
    }.isSuccess
  }

  def parse(str:String) = {
    str match {
      case "ordinary" => Ordinary
      case "forces" => Forces
      case "crown" => Crown
      case "council" => Council
      case "not-registered" => NotRegistered
      case _ => throw new IllegalArgumentException(s"$str not a valid LastRegisteredType")
    }
  }
}

case class CitizenDetails(
    dateBecameCitizen: DOB,
    howBecameCitizen: String) {
  def toApiMap = {
    dateBecameCitizen.toApiMap("dbritcrit") ++
      Map("hbritcit" -> howBecameCitizen)
  }
}
case class PassportDetails(
    passportNumber: String,
    authority: String,
    issueDate: DOB) {
  def toApiMap = {
    Map(
      "passno" -> passportNumber,
      "passloc" -> authority
    ) ++ issueDate.toApiMap("passdate")
  }
}

case class Passport(
    hasPassport: Boolean,
    bornInsideUk: Option[Boolean],
    details: Option[PassportDetails],
    citizen: Option[CitizenDetails]) {
  def toApiMap = {
    Map("bpass" -> hasPassport.toString) ++
      details.map(_.toApiMap).getOrElse(Map.empty) ++
      citizen.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class OverseasAddress(
    country: Option[String],
    addressLine1: Option[String],
    addressLine2: Option[String],
    addressLine3: Option[String],
    addressLine4: Option[String],
    addressLine5: Option[String]) {
  def toApiMap =
    Map("corrcountry" -> country.getOrElse("")) ++
    addressLine1.map(addressLine => Map("corraddressline1" -> addressLine.toString)).getOrElse(Map.empty) ++
    addressLine2.map(addressLine => Map("corraddressline2" -> addressLine.toString)).getOrElse(Map.empty) ++
    addressLine3.map(addressLine => Map("corraddressline3" -> addressLine.toString)).getOrElse(Map.empty) ++
    addressLine4.map(addressLine => Map("corraddressline4" -> addressLine.toString)).getOrElse(Map.empty) ++
    addressLine5.map(addressLine => Map("corraddressline5" -> addressLine.toString)).getOrElse(Map.empty)
}

case class OverseasName(name: Option[Name], previousName: Option[PreviousName] = None)
case class OverseasParentName(name: Option[ParentName], previousName: Option[ParentPreviousName] = None)

case class ParentName(firstName:String,
                middleNames:Option[String],
                lastName:String) {
  def toApiMap(fnKey:String, mnKey:String, lnKey:String):Map[String,String] = {
    Map(fnKey -> firstName, lnKey -> lastName) ++ middleNames.map(mn => Map(mnKey -> mn)).getOrElse(Map.empty)
  }
}

case class ParentPreviousName(hasPreviousName:Boolean,
                        previousName:Option[ParentName]) {
  def toApiMap:Map[String,String] = {
    previousName.map(pn => pn.toApiMap("pgrfn", "pgrmn", "pgrln")).getOrElse(Map.empty) ++ 
    Map("pgnc" -> hasPreviousName.toString)
  }
}

sealed abstract class ApplicationType
object ApplicationType {
  case object YoungVoter extends ApplicationType
  case object NewVoter extends ApplicationType
  case object SpecialVoter extends ApplicationType
  case object RenewerVoter extends ApplicationType
  case object DontKnow extends ApplicationType
}
