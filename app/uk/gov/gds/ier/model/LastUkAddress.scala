package uk.gov.gds.ier.model

case class LastUkAddress(
    hasUkAddress:Option[HasAddressOption],
    address:Option[PartialAddress]
)

object LastUkAddress extends ModelMapping {

  import playMappings._

  lazy val mapping = playMappings.mapping(
    keys.hasUkAddress.key -> optional(HasAddressOption.mapping),
    keys.address.key -> optional(PartialAddress.mapping)
  ) (
    LastUkAddress.apply
  ) (
    LastUkAddress.unapply
  )
}
