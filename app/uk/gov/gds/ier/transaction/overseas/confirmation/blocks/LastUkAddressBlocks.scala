package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait LastUkAddressBlocks {
  self: ConfirmationBlock =>

  def lastUkAddress = {
    ConfirmationQuestion(
      title = "Last UK Address",
      editLink = if (form(keys.lastUkAddress.manualAddress).value.isDefined) {
        routes.LastUkAddressManualController.editGet.url
      } else {
        routes.LastUkAddressSelectController.editGet.url
      },
      changeName = "your last UK address",
      content = ifComplete(keys.lastUkAddress) {
        val addressLine = form(keys.lastUkAddress.addressLine).value.orElse{
          form(keys.lastUkAddress.manualAddress).value
        }.getOrElse("")
        val postcode = form(keys.lastUkAddress.postcode).value.getOrElse("")
        s"<p>$addressLine</p><p>$postcode</p>"
      }
    )
  }
}
