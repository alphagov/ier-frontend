package uk.gov.gds.ier.model

case class LocalAuthority(
  name: Option[String],
  url: Option[String],
  addressLine1: Option[String],
  addressLine2: Option[String],
  addressLine3: Option[String],
  addressLine4: Option[String],
  postcode: Option[String],
  emailAddress: Option[String],
  phoneNumber: Option[String])



