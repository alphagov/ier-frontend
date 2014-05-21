package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.validation.constraints.OtherAddressConstraints
import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{OtherAddress, OtherAddressOption}
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import play.api.data.validation.{Invalid, Valid, Constraint}

trait OtherAddressForms extends OtherAddressConstraints {
  self:  FormKeys
    with ErrorMessages =>

  val otherAddressForm = ErrorTransformForm(
    mapping(
      keys.otherAddress.key -> optional(otherAddressMapping)
    ) (
      otherAddress => InprogressOrdinary(otherAddress = otherAddress)
    ) (
      inprogress => Some(inprogress.otherAddress)
    ).verifying(
      atLeastOneContactOptionSelected
    )
  )

  lazy val otherAddressMapping = mapping(
    keys.hasOtherAddress.key -> otherAddressOptionMapping
  ) (
    OtherAddress.apply
  ) (
    OtherAddress.unapply
  )

  lazy val otherAddressOptionMapping = text.verifying(
    otherAddressIsValid
  ).transform[OtherAddressOption](
    str => OtherAddress.parse(str),
    option => option.name
  )

  lazy val atLeastOneContactOptionSelected = Constraint[InprogressOrdinary](keys.otherAddress.key) {
    application =>
      if (application.otherAddress.isDefined) Valid
      else Invalid("ordinary_otheraddr_error_pleaseAnswer", keys.otherAddress)
  }
}
