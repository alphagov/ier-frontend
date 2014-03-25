package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority

case class InprogressCrown(
    statement: Option[CrownStatement] = None,
    address: Option[LastUkAddress] = None,
    previousAddress: Option[PartialPreviousAddress] = None,
    nationality: Option[PartialNationality] = None,
    dob: Option[DateOfBirth] = None,
    name: Option[Name] = None,
    previousName: Option[PreviousName] = None,
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
      previousAddress = this.previousAddress.orElse(other.previousAddress),
      nationality = this.nationality.orElse(other.nationality),
      dob = this.dob.orElse(other.dob),
      name = this.name.orElse(other.name),
      previousName = this.previousName.orElse(other.previousName),
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

  def displayPartner:Boolean = {
    statement.map { p =>
      (p.crownPartner || p.councilPartner) && !(p.crownServant || p.councilEmployee)
    } == Some(true)
  }
}

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

case class CrownStatement(
    crownServant: Boolean,
    crownPartner: Boolean,
    councilEmployee: Boolean,
    councilPartner: Boolean) {

  def toApiMap = {
    Map(
      "crwn" -> crownServant.toString,
      "scrwn" -> crownPartner.toString,
      "bc" -> councilEmployee.toString,
      "sbc" -> councilPartner.toString
    )
  }
}

case class Job(
    jobTitle: Option[String],
    govDepartment: Option[String]) {

  def toApiMap =
    jobTitle.map(jobTitle => Map("role" -> jobTitle.toString)).getOrElse(Map.empty) ++
    govDepartment.map(govDepartment => Map("dept" -> govDepartment.toString)).getOrElse(Map.empty)
}

case class LastUkAddress(
    // hasUkAddress - true: current UK address
    // hasUkAddress - false: last UK address
    hasUkAddress:Option[Boolean],
    address:Option[PartialAddress]
)
