package uk.gov.gds.ier.service.apiservice

import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.Statement
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.IsoNationality
import uk.gov.gds.ier.model.PostalOrProxyVote
import uk.gov.gds.ier.model.PossibleContactAddresses
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.PreviousName
import uk.gov.gds.ier.model.Service
import uk.gov.gds.ier.model.Rank
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.model.Address
import uk.gov.gds.ier.model.Contact

case class ForcesApplication(
    statement: Option[Statement],
    address: Option[Address],
    previousAddress: Option[Address],
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

    val apiMap = Map.empty ++
      statement.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      previousAddress.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      nationality.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      service.map(_.toApiMap).getOrElse(Map.empty) ++
      rank.map(_.toApiMap).getOrElse(Map.empty) ++
      contactAddress.map(_.toApiMap(address)).getOrElse(Map.empty)  ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(_.toApiMap).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty) ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      authority.map(auth => Map("gssCode" -> auth.gssId)).getOrElse(Map.empty) ++
      ip.map(ipAddress => Map("ip" -> ipAddress)).getOrElse(Map.empty) ++
      Map("applicationType" -> "forces")

    removeSpecialCharacters(apiMap)
  }
}
