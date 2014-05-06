package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes
import uk.gov.gds.ier.form.AddressHelpers

trait AddressBlocks {
  self: ConfirmationBlock with AddressHelpers =>

  def address = {
    ConfirmationQuestion(
      title = "Where do you live?",
      editLink = routes.AddressController.editGet.url,
      changeName = "where do you live?",
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
