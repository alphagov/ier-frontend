package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.validation._
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid}
import uk.gov.gds.ier.model.MovedHouseOption

trait CommonForms {
  self: FormKeys
  with ErrorMessages =>

  lazy val movedHouseMapping = text.verifying(
    str => MovedHouseOption.isValid(str)
  ).transform[MovedHouseOption](
    str => MovedHouseOption.parse(str),
    option => option.name
  ).verifying(
    movedHouseLivingThereOrNot
  )

  lazy val movedHouseLivingThereOrNot = Constraint[MovedHouseOption]("movedHouse") {
    case MovedHouseOption.YesAndLivingThere => Valid
    case MovedHouseOption.YesAndNotLivingThere => Valid
    case MovedHouseOption.NotMoved => Valid
    case _ => Invalid("Not a valid option")
  }
}
