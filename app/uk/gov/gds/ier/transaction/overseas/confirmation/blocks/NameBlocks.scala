package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait NameBlocks {
  self: ConfirmationBlock =>

  def name = {
    val nameStr = List(
      form(keys.name.firstName).value,
      form(keys.name.middleNames).value,
      form(keys.name.lastName).value
    ).flatten.mkString(" ")

    ConfirmationQuestion(
      title = "Full name",
      editLink = routes.NameController.editGet.url,
      changeName = "full name",
      content = ifComplete(keys.overseasName.name) {
        List(nameStr)
      }
    )
  }

  def previousName = {
    val havePreviousName = form(keys.previousName.hasPreviousName).value
    val prevNameStr =  havePreviousName match {
      case `hasPreviousName` => {
        List(
          form(keys.previousName.previousName.firstName).value,
          form(keys.previousName.previousName.middleNames).value,
          form(keys.previousName.previousName.lastName).value
        ).flatten.mkString(" ")
      }
      case _ => "I have not changed my name in the last 12 months"
    }
    ConfirmationQuestion(
      title = "Previous name",
      editLink = routes.NameController.editGet.url,
      changeName = "previous name",
      content = ifComplete(keys.overseasName.previousName) {
        List(prevNameStr)
      }
    )
  }

  private val hasPreviousName = Some("true")
}
