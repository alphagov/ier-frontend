package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait OpenRegisterBlocks {
  self: ConfirmationBlock =>

  def openRegister = {
    ConfirmationQuestion(
      title = "Open register",
      editLink = routes.OpenRegisterController.editGet.url,
      changeName = "open register",
      content = ifComplete(keys.openRegister) {
        if(form(keys.openRegister.optIn).value == Some("true")){
          List("I want to include my details on the open register")
        }else{
          List("I don’t want to include my details on the open register")
        }
      }
    )
  }
}
