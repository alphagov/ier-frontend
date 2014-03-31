package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import controllers.step.overseas.routes

trait ParentNameBlocks {
  self: ConfirmationBlock =>

  def parentName = {
    val nameStr = List(
      form(keys.overseasParentName.parentName.firstName).value,
      form(keys.overseasParentName.parentName.middleNames).value,
      form(keys.overseasParentName.parentName.lastName).value
    ).flatten.mkString(" ")

    Some(ConfirmationQuestion(
      title = "Parent's or guardian's name",
      editLink = routes.ParentNameController.editGet.url,
      changeName = "full name",
      content = ifComplete(keys.overseasParentName.parentName) {
        s"<p>$nameStr</p>"
      }
    ))
  }

  def parentPreviousName = {
    val havePreviousName = form(keys.overseasParentName.parentPreviousName.hasPreviousName).value
    val prevNameStr =  havePreviousName match {
      case `hasPreviousName` => {
        List(
          form(keys.overseasParentName.parentPreviousName.previousName.firstName).value,
          form(keys.overseasParentName.parentPreviousName.previousName.middleNames).value,
          form(keys.overseasParentName.parentPreviousName.previousName.lastName).value
        ).flatten.mkString(" ")
      }
      case _ => "They haven't changed their name since they left the UK"
    }
    Some(ConfirmationQuestion(
      title = "Parent's or guardian's previous name",
      editLink = routes.ParentNameController.editGet.url,
      changeName = "previous name",
      content = ifComplete(keys.overseasParentName.parentPreviousName) {
        s"<p>$prevNameStr</p>"
      }
    ))
  }
  
  private val under18 = Some(true)
  private val withLimit = Some(true)
  private val hasPreviousName = Some("true")
}
