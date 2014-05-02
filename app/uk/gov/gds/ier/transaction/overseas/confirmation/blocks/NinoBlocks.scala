package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait NinoBlocks {
  self: ConfirmationBlock =>

  def nino = {
    ConfirmationQuestion(
      title = "National Insurance number",
      editLink = routes.NinoController.editGet.url,
      changeName = "national insurance number",
      content = ifComplete(keys.nino) {
        if(form(keys.nino.nino).value.isDefined){
          List(form(keys.nino.nino).value.getOrElse(""))
        } else {
          List("I cannot provide my national insurance number because:",
            form(keys.nino.noNinoReason).value.getOrElse(""))
        }
      }
    )
  }

}
