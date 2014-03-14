package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority

case class InprogressCrown(
    statement: Option[CrownStatement] = None,
    address: Option[PartialAddress] = None,
    nationality: Option[PartialNationality] = None,
    dob: Option[DateOfBirth] = None,
    name: Option[Name] = None,
    job: Option[Job] = None,
    nino: Option[Nino] = None,
    contactAddress: Option[PossibleContactAddresses] = None,
    openRegisterOptin: Option[Boolean] = None,
    waysToVote: Option[WaysToVote] = None,
    postalOrProxyVote: Option[PostalOrProxyVote] = None,
    contact: Option[Contact] = None,
    possibleAddresses: Option[PossibleAddress] = None)
  extends InprogressApplication[InprogressCrown] {

  def merge(other:InprogressCrown) = {
    other.copy(
      statement = this.statement.orElse(other.statement),
      address = this.address.orElse(other.address),
      nationality = this.nationality.orElse(other.nationality),
      dob = this.dob.orElse(other.dob),
      name = this.name.orElse(other.name),
      job = this.job.orElse(other.job),
      nino = this.nino.orElse(other.nino),
      contactAddress = this.contactAddress.orElse(other.contactAddress),
      openRegisterOptin = this.openRegisterOptin.orElse(other.openRegisterOptin),
      waysToVote = this.waysToVote.orElse(other.waysToVote),
      postalOrProxyVote = this.postalOrProxyVote.orElse(other.postalOrProxyVote),
      contact = this.contact.orElse(other.contact),
      possibleAddresses = None
    )
  }
}

case class CrownApplication(
    statement: Option[CrownStatement],
    address: Option[Address],
    nationality: Option[IsoNationality],
    dob: Option[DateOfBirth],
    name: Option[Name],
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
      job.map(_.toApiMap).getOrElse(Map.empty) ++
      nino.map(_.toApiMap).getOrElse(Map.empty) ++
      mapContactAddress ++
      openRegisterOptin.map(open => Map("opnreg" -> open.toString)).getOrElse(Map.empty) ++
      postalOrProxyVote.map(_.toApiMap).getOrElse(Map.empty) ++
      contact.map(_.toApiMap).getOrElse(Map.empty) ++
      referenceNumber.map(refNum => Map("refNum" -> refNum)).getOrElse(Map.empty) ++
      authority.map(auth => Map("gssCode" -> auth.gssId)).getOrElse(Map.empty) ++
      ip.map(ipAddress => Map("ip" -> ipAddress)).getOrElse(Map.empty) ++
      Map("applicationType" -> "crown")
  }
}



case class CrownStatement(
    crownMember: Option[Boolean],
    partnerCrownMember: Option[Boolean],
    britishCouncilMember: Option[Boolean],
    partnerBritishCouncilMember: Option[Boolean]) {

  def toApiMap =
    isCrownPartner ++ isBritishCouncilPartner ++
      britishCouncilMember.map(britishCouncilFlag =>
        Map("bc" -> britishCouncilFlag.toString)).getOrElse(Map("bc" -> "false"))

  def isCrownPartner: Map[String, String] = {
    val isCrownPartner = Some(true)
    val isNotCrownMember = Some(false)
    ( partnerCrownMember, crownMember ) match {
      case (`isCrownPartner`, `isNotCrownMember`) =>  Map("scrwn" -> "true")
      case (`isCrownPartner`, None) =>  Map("scrwn" -> "true")
      case _ => Map("scrwn" -> "false")
    }
  }

  def isBritishCouncilPartner: Map[String, String] = {
    val isBritishCouncilPartner = Some(true)
    val isNotBritishCouncilMember = Some(false)
    ( partnerBritishCouncilMember, britishCouncilMember ) match {
      case (`isBritishCouncilPartner`, `isNotBritishCouncilMember`) => Map("sbc" -> "true")
      case (`isBritishCouncilPartner`, None) => Map("sbc" -> "true")
      case _ => Map("sbc" -> "false")
    }
  }
}

case class Job(
    jobTitle: Option[String],
    govDepartment: Option[String]) {

  def toApiMap =
    jobTitle.map(jobTitle => Map("role" -> jobTitle.toString)).getOrElse(Map.empty) ++
    govDepartment.map(govDepartment => Map("dept" -> govDepartment.toString)).getOrElse(Map.empty)
}
