package uk.gov.gds.ier.service.apiservice

import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.DateOfBirth
import uk.gov.gds.ier.model.CrownStatement
import uk.gov.gds.ier.model.IsoNationality
import uk.gov.gds.ier.model.PostalOrProxyVote
import uk.gov.gds.ier.model.PossibleContactAddresses
import uk.gov.gds.ier.model.Name
import uk.gov.gds.ier.model.PreviousName
import uk.gov.gds.ier.model.Job
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.model.Address
import uk.gov.gds.ier.model.Contact

case class CrownApplication(
    statement: Option[CrownStatement],
    address: Option[Address],
    previousAddress: Option[Address],
    nationality: Option[IsoNationality],
    dob: Option[DateOfBirth],
    name: Option[Name],
    previousName: Option[PreviousName],
    job: Option[Job],
    nino: Option[Nino],
    contactAddress: Option[PossibleContactAddresses],
    openRegisterOptin: Option[Boolean],
    postalOrProxyVote: Option[PostalOrProxyVote],
    contact: Option[Contact],
    referenceNumber: Option[String],
    authority: Option[LocalAuthority],
    ip: Option[String])
  extends CompleteApplication {

  def toApiMap = {

    Map.empty ++
      statement.map(_.toApiMap).getOrElse(Map.empty) ++
      address.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      previousAddress.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      nationality.map(_.toApiMap).getOrElse(Map.empty) ++
      dob.map(_.toApiMap).getOrElse(Map.empty) ++
      name.map(_.toApiMap("fn", "mn", "ln")).getOrElse(Map.empty) ++
      previousName.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      job.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      contactAddress.map(_.toApiMap(address)).getOrElse(Map.empty) ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(_.toApiMap).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty) ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      authority.map(auth => Map("gssCode" -> auth.gssId)).getOrElse(Map.empty) ++
      ip.map(ipAddress => Map("ip" -> ipAddress)).getOrElse(Map.empty) ++
      Map("applicationType" -> "crown")
  }
}
