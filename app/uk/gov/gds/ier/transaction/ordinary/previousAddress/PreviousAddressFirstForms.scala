package uk.gov.gds.ier.transaction.ordinary.previousAddress

import play.api.data.Forms._
import uk.gov.gds.ier.model.{MovedHouseOption, PartialAddress, PartialPreviousAddress}
import uk.gov.gds.ier.validation.{PostcodeValidator, ErrorMessages, FormKeys, ErrorTransformForm}
import uk.gov.gds.ier.serialiser.WithSerialiser
import uk.gov.gds.ier.validation.constraints.CommonConstraints
import play.api.data.validation.{Invalid, Valid, Constraint}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait PreviousAddressFirstForms
    extends PreviousAddressFirstConstraints
    with CommonForms {
  self: FormKeys
    with ErrorMessages
    with WithSerialiser =>

  lazy val movedHouseRegisteredAbroadMapping = mapping(
    keys.movedRecently.key -> optional(MovedHouseOption.mapping),
    keys.wasRegisteredWhenAbroad.key -> optional(boolean)
  ) (
      (movedHouse, registered) => movedHouse match {
        case Some(MovedHouseOption.MovedFromAbroad) =>
          registered match {
            case Some(true) => Some(MovedHouseOption.MovedFromAbroadRegistered)
            case Some(false) => Some(MovedHouseOption.MovedFromAbroadNotRegistered)
            case _ => movedHouse
          }
        case _ => movedHouse
      }
    ) (
      (movedHouse) => movedHouse match {
        case Some(MovedHouseOption.MovedFromAbroadRegistered) => Some((Some(MovedHouseOption.MovedFromAbroad), Some(true)))
        case Some(MovedHouseOption.MovedFromAbroadNotRegistered) => Some((Some(MovedHouseOption.MovedFromAbroad), Some(false)))
        case _ => Some((movedHouse, None))
      }
    )

  lazy val previousAddressRegisteredAbroadMapping = mapping(
    keys.movedRecently.key -> movedHouseRegisteredAbroadMapping,
    keys.previousAddress.key -> optional(PartialAddress.mapping)
  ) (
    PartialPreviousAddress.apply
  ) (
    PartialPreviousAddress.unapply
  )

  val previousAddressFirstForm = ErrorTransformForm(
    mapping (
      keys.previousAddress.key -> optional(previousAddressRegisteredAbroadMapping)
    ) (
      previousAddressYesNo => InprogressOrdinary(
        previousAddress = previousAddressYesNo
      )
    ) (
      inprogress => Some(inprogress.previousAddress)
    ).verifying(
      previousAddressYesNoIsNotEmpty,
      previouslyRegisteredAbroad)
  )
}

trait PreviousAddressFirstConstraints extends CommonConstraints {
  self: FormKeys
    with ErrorMessages =>

  lazy val previousAddressYesNoIsNotEmpty = Constraint[InprogressOrdinary](keys.previousAddress.movedRecently.key) {
    inprogress => inprogress.previousAddress match {
      case Some(PartialPreviousAddress(Some(_), _)) => Valid
      case _ => Invalid("Please answer this question",keys.previousAddress.movedRecently)
    }
  }

  lazy val previouslyRegisteredAbroad = Constraint[InprogressOrdinary](keys.previousAddress.wasRegisteredWhenAbroad.key) {
    inprogress => inprogress.previousAddress.map( _.movedRecently ) match {
      case Some(Some(MovedHouseOption.MovedFromAbroad)) => Invalid("Please answer this question",keys.previousAddress.wasRegisteredWhenAbroad)
      case _ => Valid
    }
  }
}
