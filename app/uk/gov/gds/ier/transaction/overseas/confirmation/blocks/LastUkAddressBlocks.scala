package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes
import uk.gov.gds.ier.form.AddressHelpers

trait LastUkAddressBlocks extends AddressHelpers {
  self: ConfirmationBlock =>

  def lastUkAddress = {
    ConfirmationQuestion(
      title = "Registration address",
      editLink = if (isManualAddressDefined(form, keys.lastUkAddress.manualAddress)) {
        routes.LastUkAddressManualController.editGet.url
      } else {
        routes.LastUkAddressSelectController.editGet.url
      },
      changeName = "your registration address",
      content = ifComplete(keys.lastUkAddress) {
        val addressLine = form(keys.lastUkAddress.addressLine).value.orElse{
          manualAddressToOneLine(form, keys.lastUkAddress.manualAddress)
        }.getOrElse("")
        val postcode = form(keys.lastUkAddress.postcode).value.getOrElse("")
        List(addressLine, postcode)
      }
    )
  }
}
