package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes
import uk.gov.gds.ier.form.AddressHelpers

trait LastUkAddressBlocks extends AddressHelpers {
  self: ConfirmationBlock =>

  def lastUkAddress = {
    Some(ConfirmationQuestion(
      title = "Last UK Address",
      editLink = if (isManualAddressDefined(form, keys.lastUkAddress.manualAddress)) {
        routes.LastUkAddressManualController.editGet.url
      } else {
        routes.LastUkAddressSelectController.editGet.url
      },
      changeName = "your last UK address",
      content = ifComplete(keys.lastUkAddress) {
        val addressLine = form(keys.lastUkAddress.addressLine).value.orElse{
          manualAddressToOneLine(form, keys.lastUkAddress.manualAddress)
        }.getOrElse("")
        val postcode = form(keys.lastUkAddress.postcode).value.getOrElse("")
        s"<p>$addressLine</p><p>$postcode</p>"
      }
    ))
  }
}
