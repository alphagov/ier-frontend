package uk.gov.gds.ier.service.apiservice

import uk.gov.gds.common.model.LocalAuthority
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.OverseasAddress
import uk.gov.gds.ier.model.PreviouslyRegistered
import uk.gov.gds.ier.model.Passport
import uk.gov.gds.ier.model.PostalOrProxyVote
import uk.gov.gds.ier.model.OverseasName
import uk.gov.gds.ier.model.DateLeft
import uk.gov.gds.ier.model.OverseasParentName
import uk.gov.gds.ier.model.DateLeftSpecial
import uk.gov.gds.ier.model.LastRegisteredToVote
import uk.gov.gds.ier.model.DOB
import uk.gov.gds.ier.model.Nino
import uk.gov.gds.ier.model.Address
import uk.gov.gds.ier.model.Contact

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
    lastUkAddress: Option[Address] = None,
    parentsAddress: Option[Address] = None,
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
      overseasName.map(_.toApiMap("p")).getOrElse(Map.empty) ++
      previouslyRegistered.map(_.toApiMap(lastRegisteredToVote)).getOrElse(Map.empty) ++
      dateLeftUk.map(_.toApiMap()).getOrElse(Map.empty) ++
      dateLeftSpecial.map(_.toApiMap).getOrElse(Map.empty) ++
      overseasParentName.map(_.toApiMap("pgr")).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      lastUkAddress.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      dob.map(_.toApiMap("dob")).getOrElse(Map.empty) ++
      address.map(_.toApiMap).getOrElse(Map.empty) ++
      lastUkAddress.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
      parentsAddress.map(_.toApiMap("reg")).getOrElse(Map.empty) ++
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
