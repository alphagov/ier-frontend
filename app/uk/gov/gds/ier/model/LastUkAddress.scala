package uk.gov.gds.ier.model

case class LastUkAddress(
    // hasUkAddress - true: current UK address
    // hasUkAddress - false: last UK address
    hasUkAddress:Option[Boolean],
    address:Option[PartialAddress]
)

object LastUkAddress extends ModelMapping {

  import playMappings._

  lazy val mapping = playMappings.mapping(
    keys.hasUkAddress.key -> optional(boolean),
    keys.address.key -> optional(PartialAddress.mapping)
  ) (
    LastUkAddress.apply
  ) (
    LastUkAddress.unapply
  )
}
