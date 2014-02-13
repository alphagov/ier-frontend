package uk.gov.gds.ier.model

import scala.util.Try
import uk.gov.gds.ier.model.WaysToVoteType.WaysToVoteType
import com.fasterxml.jackson.core.`type`.TypeReference
import com.fasterxml.jackson.module.scala.JsonScalaEnumeration

case class InprogressOverseas(
    name: Option[Name] = None,
    previousName: Option[PreviousName] = None,
    previouslyRegistered: Option[PreviouslyRegistered] = None,
    dateLeftSpecial: Option[DateLeftSpecial] = None,
    dateLeftUk: Option[DateLeft] = None,
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
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      previouslyRegistered = this.previouslyRegistered.orElse(other.previouslyRegistered),
      dateLeftSpecial = this.dateLeftSpecial.orElse(other.dateLeftSpecial),
      dateLeftUk = this.dateLeftUk.orElse(other.dateLeftUk),
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
    name: Option[Name],
    previousName: Option[PreviousName],
    previouslyRegistered: Option[PreviouslyRegistered],
    dateLeftUk: Option[DateLeft],
    dateLeftSpecial: Option[DateLeftSpecial],
    lastRegisteredToVote: Option[LastRegisteredToVote],
    dob: Option[DOB],
    nino: Option[Nino],
    address: Option[OverseasAddress],
    lastUkAddress: Option[PartialAddress] = None,
    openRegisterOptin: Option[Boolean],
    waysToVote: Option[WaysToVote],
    postalOrProxyVote: Option[PostalOrProxyVote],
    contact: Option[Contact])
  extends CompleteApplication {

  def toApiMap = {
    Map.empty ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap).getOrElse(Map.empty) ++
      previouslyRegistered.map(_.toApiMap).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap()).getOrElse(Map.empty) ++
      dateLeftSpecial.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      lastRegisteredToVote.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(postalOrProxyVote => postalOrProxyVote.postalVoteOption.map(
        postalVoteOption => Map(postalOrProxyVote.apiVoteKey -> postalVoteOption.toString))
          .getOrElse(Map.empty)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(postalOrProxyVote => postalOrProxyVote.deliveryMethod.map(
        deliveryMethod => deliveryMethod.emailAddress.map(
          emailAddress => Map(postalOrProxyVote.apiEmailKey -> emailAddress)).getOrElse(Map.empty))
            .getOrElse(Map.empty)).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty)
  }
}

case class Stub() {
  def toApiMap = Map.empty
}

case class PreviouslyRegistered(hasPreviouslyRegistered: Boolean) {
  def toApiMap = {
    if (hasPreviouslyRegistered) Map("povseas" -> "true")
    else Map("povseas" -> "false")
  }
}

case class DateLeftSpecial (date:DateLeft, registeredType:LastRegisteredType) {
  def toApiMap = {
    date.toApiMap("dcs")
  }
}

case class DateLeft (year:Int, month:Int) {
  def toApiMap(key:String = "leftUk") = {
    Map(key -> "%04d-%02d".format(year,month))
  }
}

case class LastRegisteredToVote (lastRegisteredType:LastRegisteredType) {
  def toApiMap = Map.empty
}

sealed case class LastRegisteredType(name:String)

object LastRegisteredType {
  val UK = LastRegisteredType("uk")
  val Army = LastRegisteredType("army")
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
      case "uk" => UK
      case "army" => Army
      case "crown" => Crown
      case "council" => Council
      case "not-registered" => NotRegistered
      case _ => throw new IllegalArgumentException(s"$str not a valid LastRegisteredType")
    }
  }
}

case class CitizenDetails(
    dateBecameCitizen: DOB,
    howBecameCitizen: String
)
case class PassportDetails(
    passportNumber: String,
    authority: String,
    issueDate: DOB
)

case class Passport(
    hasPassport: Boolean,
    bornInsideUk: Option[Boolean],
    details: Option[PassportDetails],
    citizen: Option[CitizenDetails]
)

case class OverseasAddress(
    country: Option[String],
    addressDetails: Option[String]) {
  def toApiMap = Map(
    "corrcountry" -> country.getOrElse(""),
    "corraddress" -> addressDetails.getOrElse("")
  )
}

// TODO: review using of Json specific stuff here, look for alternatives
case class WaysToVote (
  @JsonScalaEnumeration(classOf[WaysToVoteTypeRef]) waysToVoteType: WaysToVoteType) {
}

class WaysToVoteTypeRef extends TypeReference[WaysToVoteType.type]
object WaysToVoteType extends Enumeration {
  type WaysToVoteType = Value
  val InPerson = Value("in-person")
  val ByPost = Value("by-post")
  val ByProxy = Value("by-proxy")
}

case class CountryWithCode(
    country: String,
    code: String
)

case class PostalOrProxyVote (
    typeVote: String,
    postalVoteOption: Option[Boolean],
    deliveryMethod: Option[PostalVoteDeliveryMethod]) {

  def apiVoteKey = {
    typeVote match {
      case "postal" => "pvote"
      case "proxy" => "proxyvote"
      case _ => throw new IllegalArgumentException()
    }
  }

  def apiEmailKey = {
    typeVote match {
      case "postal" => "pvoteemail"
      case "proxy" => "proxyvoteemail"
      case _ => throw new IllegalArgumentException()
    }
  }
}
