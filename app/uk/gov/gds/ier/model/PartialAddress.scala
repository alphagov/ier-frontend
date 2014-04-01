package uk.gov.gds.ier.model

case class PartialAddress(
    addressLine: Option[String],
    uprn: Option[String],
    postcode: String,
    manualAddress: Option[PartialManualAddress]
)

object PartialAddress extends ModelMapping {
  import playMappings._

  lazy val mapping = playMappings.mapping(
    keys.addressLine.key -> optional(nonEmptyText),
    keys.uprn.key -> optional(nonEmptyText),
    keys.postcode.key -> nonEmptyText,
    keys.manualAddress.key -> optional(PartialManualAddress.mapping)
  ) (
    PartialAddress.apply
  ) (
    PartialAddress.unapply
  )
}