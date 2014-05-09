package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes
import uk.gov.gds.ier.form.AddressHelpers

trait ParentsAddressBlocks extends AddressHelpers {
  self: ConfirmationBlock =>

  def parentsAddress = {
    val editCall = if (manualAddressToOneLine(form, keys.parentsAddress.manualAddress).isDefined) {
      routes.ParentsAddressManualController.editGet
    } else if (form(keys.parentsAddress.uprn).value.isDefined) {
      routes.ParentsAddressSelectController.editGet
    } else {
      routes.ParentsAddressController.editGet
    }

    ConfirmationQuestion(
      title = "Parent's or guardian's last UK address",
      editLink = editCall.url,
      changeName = "your parent's or guardian's last UK address",
      content = ifComplete(keys.parentsAddress) {
        val addressLine = form(keys.parentsAddress.addressLine).value.orElse{
          manualAddressToOneLine(form, keys.parentsAddress.manualAddress)
        }.getOrElse("")
        val postcode = form(keys.parentsAddress.postcode).value.getOrElse("")
        List(addressLine, postcode)
      }
    )
  }
}
