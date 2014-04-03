package uk.gov.gds.ier.model

import uk.gov.gds.common.model.LocalAuthority

case class OrdinaryApplication(
    name: Option[Name],
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
      previousName.map(_.toApiMap("p")).getOrElse(Map.empty) ++
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
