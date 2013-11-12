package uk.gov.gds.ier.step.otherAddress

import uk.gov.gds.ier.validation.{ErrorMessages, FormKeys}
import uk.gov.gds.ier.model.{InprogressApplication, OtherAddress}
import uk.gov.gds.ier.serialiser.WithSerialiser
import play.api.data.Form
import play.api.data.Forms._

trait OtherAddressForms {
  self:  FormKeys
    with ErrorMessages
    with WithSerialiser =>

  lazy val otherAddressMapping = mapping(
    keys.hasOtherAddress.key -> boolean
  ) (OtherAddress.apply) (OtherAddress.unapply)

  val otherAddressForm = Form(
    mapping(
      keys.otherAddress.key -> optional(otherAddressMapping)
        .verifying("Please answer this question", otherAddress => otherAddress.isDefined)
    ) (
      otherAddress => InprogressApplication(otherAddress = otherAddress)
    ) (
      inprogress => Some(inprogress.otherAddress)
    )
  )
}