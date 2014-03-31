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

        val result:StringBuilder = new StringBuilder
        result.append ("<p>")
        result.append (
          List (
            form(keys.overseasAddress.addressLine1).value,
            form(keys.overseasAddress.addressLine2).value,
            form(keys.overseasAddress.addressLine3).value,
            form(keys.overseasAddress.addressLine4).value,
            form(keys.overseasAddress.addressLine5).value)
          .filter(!_.getOrElse("").isEmpty).map(_.get).mkString("","<br/>",""))
        result.append ("</p>")
        result.append ("<p>" + form (keys.overseasAddress.country).value.getOrElse("") + "</p>")
        result.toString()
      }
    )
  }
}
