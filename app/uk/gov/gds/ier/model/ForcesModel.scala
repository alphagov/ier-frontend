package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority
import scala.util.Try

case class InprogressForces(
    statement: Option[Statement] = None,
    address: Option[PartialAddress] = None,
    nationality: Option[PartialNationality] = None,
    dob: Option[DateOfBirth] = None,
    name: Option[Name] = None,
    previousName: Option[PreviousName] = None,
    nino: Option[Nino] = None,
    service: Option[Service] = None,
    rank: Option[Rank] = None,
    contactAddress: Option[PossibleContactAddresses] = None,
    openRegisterOptin: Option[Boolean] = None,
    waysToVote: Option[WaysToVote] = None,
    postalOrProxyVote: Option[PostalOrProxyVote] = None,
    contact: Option[Contact] = None,
    possibleAddresses: Option[PossibleAddress] = None)
  extends InprogressApplication[InprogressForces] {

  def merge(other:InprogressForces) = {
    other.copy(
      statement = this.statement.orElse(other.statement),
      address = this.address.orElse(other.address),
      nationality = this.nationality.orElse(other.nationality),
      dob = this.dob.orElse(other.dob),
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
      nino = this.nino.orElse(other.nino),
      service = this.service.orElse(other.service),
      rank = this.rank.orElse(other.rank),
      contactAddress = this.contactAddress.orElse(other.contactAddress),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      waysToVote = this.waysToVote.orElse(other.waysToVote),
      postalOrProxyVote = this.postalOrProxyVote.orElse(other.postalOrProxyVote),
      contact = this.contact.orElse(other.contact),
      possibleAddresses = None
    )
  }
}

case class ForcesApplication(
    statement: Option[Statement],
    address: Option[Address],
    nationality: Option[IsoNationality],
    dob: Option[DateOfBirth],
    name: Option[Name],
    previousName: Option[PreviousName],
    nino: Option[Nino],
    service: Option[Service],
    rank: Option[Rank],
    contactAddress: Option[PossibleContactAddresses],
    openRegisterOptin: Option[Boolean],
    postalOrProxyVote: Option[PostalOrProxyVote],
    contact: Option[Contact],
    referenceNumber: Option[String],
    authority: Option[LocalAuthority],
    ip: Option[String])
  extends CompleteApplication {

  def toApiMap = {

    val mapContactAddress =
      if (contactAddress.isDefined && contactAddress.map(_.contactAddressType.exists(value => value.equals("uk"))).get)
        contactAddress.map(_.toApiMapFromUkAddress(address)).getOrElse(Map.empty)
      else
        contactAddress.map(_.toApiMap).getOrElse(Map.empty)

    Map.empty ++
      statement.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      nationality.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      service.map(_.toApiMap).getOrElse(Map.empty) ++
      rank.map(_.toApiMap).getOrElse(Map.empty) ++
      mapContactAddress  ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(_.toApiMap).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty) ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      authority.map(auth => Map("gssCode" -> auth.gssId)).getOrElse(Map.empty) ++
      ip.map(ipAddress => Map("ip" -> ipAddress)).getOrElse(Map.empty) ++
      Map("applicationType" -> "forces")
  }
}

case class Statement(
    memberForcesFlag: Option[Boolean],
    partnerForcesFlag: Option[Boolean]) {
  def toApiMap =
    partnerForcesFlag.map(partnerForcesFlag => Map("saf" -> partnerForcesFlag.toString))
      .getOrElse( Map("saf" -> "false"))
}

case class Service(
    serviceName: Option[ServiceType],
    regiment: Option[String]) {
  def toApiMap =
    serviceName.map(serviceName => Map("serv" -> serviceName.name)).getOrElse(Map.empty) ++
    regiment.map(regiment => Map("reg" -> regiment.toString)).getOrElse(Map.empty)
}

case class Rank(
    serviceNumber: Option[String],
    rank: Option[String]) {
  def toApiMap =
    serviceNumber.map(serviceNumber => Map("servno" -> serviceNumber.toString)).getOrElse(Map.empty) ++
    rank.map(rank => Map("rank" -> rank.toString)).getOrElse(Map.empty)
}

sealed case class ServiceType(name:String)

object ServiceType {
  val RoyalNavy = ServiceType("Royal Navy")
  val BritishArmy = ServiceType("British Army")
  val RoyalAirForce = ServiceType("Royal Air Force")

  def isValid(str:String) = {
    Try {
      parse(str)
    }.isSuccess
  }

  def parse(str:String) = {
    str match {
      case "Royal Navy" => RoyalNavy
      case "British Army" => BritishArmy
      case "Royal Air Force" => RoyalAirForce
      case _ => throw new IllegalArgumentException(s"$str not a valid ServiceType")
    }
  }
}
