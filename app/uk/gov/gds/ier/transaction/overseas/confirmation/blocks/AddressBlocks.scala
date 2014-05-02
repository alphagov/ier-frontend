package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait AddressBlocks {
  self: ConfirmationBlock =>

  def address = {
    ConfirmationQuestion(
      title = "Where do you live?",
      editLink = routes.AddressController.editGet.url,
      changeName = "where do you live?",
      content = ifComplete(keys.overseasAddress) {
        List(
          // address lines separated are concatenated by comma and go to one paragraph
          List(
            form(keys.overseasAddress.addressLine1).value,
            form(keys.overseasAddress.addressLine2).value,
            form(keys.overseasAddress.addressLine3).value,
            form(keys.overseasAddress.addressLine4).value,
            form(keys.overseasAddress.addressLine5).value
          ).flatten.mkString(", ") match {
            case "" => None
            case a => Some(a)
            // FIXME: improve!
          },

          // country goes to a separate paragraph
          form(keys.overseasAddress.country).value
        ).flatten
      }
    )
  }

}
