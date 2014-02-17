package uk.gov.gds.ier.transaction.overseas.dateLeftSpecial

import uk.gov.gds.ier.model._
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages, ErrorTransformForm}
import play.api.data.Forms._

trait DateLeftSpecialForms {
  lazy val dateLeftSpecialMapping = mapping(
      "foo" -> text
  ) (
    foo => DateLeftSpecial(
      DateLeft(1990, 1),
      LastRegisteredType.Ordinary
    )
  ) (
    dateLeftSpecial => Some("foo")
  )
}
