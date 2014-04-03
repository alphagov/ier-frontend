package uk.gov.gds.ier.model

case class PartialAddress(
    addressLine: Option[String],
    uprn: Option[String],
    postcode: String,
    manualAddress: Option[PartialManualAddress])

case class PartialManualAddress(
    lineOne: Option[String] = None,
    lineTwo: Option[String] = None,
    lineThree: Option[String] = None,
    city: Option[String] = None)

case class PartialPreviousAddress (
    movedRecently:Option[MovedHouseOption],
    previousAddress:Option[PartialAddress])








