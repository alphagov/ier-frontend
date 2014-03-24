package uk.gov.gds.ier.transaction.ordinary.previousAddress

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import uk.gov.gds.ier.model.MovedHouseOption

trait CommonForms {
  self: FormKeys
  with ErrorMessages =>

  lazy val movedHouseMapping = text.verifying(
    str => MovedHouseOption.isValid(str)
  ).transform[MovedHouseOption](
    str => MovedHouseOption.parse(str),
    option => option.name
  )

}
