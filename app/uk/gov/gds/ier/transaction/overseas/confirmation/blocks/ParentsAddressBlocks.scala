package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait ParentsAddressBlocks {
  self: ConfirmationBlock =>

  def parentsAddress = {
    val editCall = if (form(keys.parentsAddress.manualAddress).value.isDefined) {
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
          form(keys.parentsAddress.manualAddress).value
        }.getOrElse("")
        val postcode = form(keys.parentsAddress.postcode).value.getOrElse("")
        s"<p>$addressLine</p><p>$postcode</p>"
      }
    )
  }
}
