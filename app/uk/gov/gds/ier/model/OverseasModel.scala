package uk.gov.gds.ier.model

import uk.gov.gds.ier.model.LastRegisteredType.LastRegisteredType
import scala.util.Try

case class InprogressOverseas(
    name: Option[Name] = None,
    previousName: Option[PreviousName] = None,
    previouslyRegistered: Option[PreviouslyRegistered] = None,
    dateLeftUk: Option[DateLeftUk] = None,
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
    dateLeftUk: Option[DateLeftUk],
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
      dateLeftUk.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      lastRegisteredToVote.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(_.toApiMap).getOrElse(Map.empty) ++
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


case class DateLeftUk (year:Int, month:Int) {
  def toApiMap = {
    Map("dlu" -> "%04d-%02d".format(year,month))
  }
}

case class LastRegisteredToVote (lastRegisteredType:LastRegisteredType) {
  def toApiMap = Map.empty
}

object LastRegisteredType extends Enumeration {
  type LastRegisteredType = Value
  val UK = Value("uk")
  val Army = Value("army")
  val Crown = Value("crown")
  val Council = Value("council")
  val NotRegistered = Value("not-registered")
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

case class WaysToVote (waysToVoteType: WaysToVoteType)

sealed case class WaysToVoteType(name:String)
object WaysToVoteType {
  val InPerson = WaysToVoteType("in-person")
  val ByPost = WaysToVoteType("by-post")
  val ByProxy = WaysToVoteType("by-proxy")

  def parse(str: String) = {
    str match {
      case "in-person" => InPerson
      case "by-proxy" => ByProxy
      case "by-post" => ByPost
    }
  }
  def isValid(str: String) = {
    Try{ parse(str) }.isSuccess
  }
}

case class CountryWithCode(
    country: String,
    code: String
)

case class PostalOrProxyVote (
    typeVote: WaysToVoteType,
    postalVoteOption: Option[Boolean],
    deliveryMethod: Option[PostalVoteDeliveryMethod]) {

  def toApiMap = {
    val voteMap = postalVoteOption match {
      case Some(pvote) => typeVote match {
        case WaysToVoteType.ByPost => Map("pvote" -> pvote.toString)
        case WaysToVoteType.ByProxy => Map("proxyvote" -> pvote.toString)
        case _ => Map.empty
      }
      case _ => Map.empty
    }
    val emailMap = deliveryMethod.flatMap(_.emailAddress) match {
      case Some(email) => typeVote match {
        case WaysToVoteType.ByPost => Map("pvoteemail" -> email)
        case WaysToVoteType.ByProxy => Map("proxyvoteemail" -> email)
        case _ => Map.empty
      }
      case _ => Map.empty
    }
    voteMap ++ emailMap
  }
}
