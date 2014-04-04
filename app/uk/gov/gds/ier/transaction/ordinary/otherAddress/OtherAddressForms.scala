package uk.gov.gds.ier.transaction.ordinary.otherAddress

import uk.gov.gds.ier.validation.constraints.OtherAddressConstraints
import uk.gov.gds.ier.validation.{ErrorTransformForm, ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{OtherAddress, OtherAddressOption}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

trait OtherAddressForms extends OtherAddressConstraints {
  self:  FormKeys
    with ErrorMessages
    with WithSerialiser =>

  val otherAddressOptionMapping = text.verifying(
    otherAddressIsValid
  ).transform[OtherAddressOption](
    str => OtherAddress.parse(str),
    option => option.name
  )

  lazy val otherAddressMapping = mapping(
    keys.hasOtherAddress.key -> otherAddressOptionMapping
  ) (OtherAddress.apply) (OtherAddress.unapply)

  val otherAddressForm = ErrorTransformForm(
    mapping(
      keys.otherAddress.key -> optional(otherAddressMapping)
        .verifying("Please answer this question", otherAddress => otherAddress.isDefined)
    ) (
      otherAddress => InprogressOrdinary(otherAddress = otherAddress)
    ) (
      inprogress => Some(inprogress.otherAddress)
    )
  )
}
