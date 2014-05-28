package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes
import uk.gov.gds.ier.form.AddressHelpers

trait AddressBlocks {
  self: ConfirmationBlock with AddressHelpers =>

  def address = {
    ConfirmationQuestion(
      title = "Correspondence address",
      editLink = routes.AddressController.editGet.url,
      changeName = "correspondence address",
      content = ifComplete(keys.overseasAddress) {
        List(
          // address lines separated are concatenated by comma and go to one paragraph
          concatAddressToOneLine(form, keys.overseasAddress),
          // country goes to a separate paragraph
          form(keys.overseasAddress.country).value
        ).flatten
      }
    )
  }
}
