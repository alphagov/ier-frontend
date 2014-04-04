package uk.gov.gds.ier.model

case class LastUkAddress(
    // hasUkAddress - true: current UK address
    // hasUkAddress - false: last UK address
    hasUkAddress:Option[Boolean],
    address:Option[PartialAddress]
)
