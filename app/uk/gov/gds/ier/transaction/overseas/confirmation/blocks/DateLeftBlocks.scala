package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model.LastRegisteredType._
import controllers.step.overseas.routes

trait DateLeftBlocks {
  self: ConfirmationBlock =>

  def dateLeftSpecial = {
    form.lastRegisteredType match {
      case Some(Forces) => dateLeftForces
      case Some(Crown) => dateLeftCrown
      case Some(Council) => dateLeftCouncil
      case _ => throw new IllegalArgumentException(
        "Last registered type invalid, must be either Forces, Crown or Council"
      )
    }
  }

  def dateLeftUk = {
    val yearMonth = form.dateLeftUk map { _.toString("MMMM, yyyy") } getOrElse ""

    ConfirmationQuestion(
      title = "Date you left the UK",
      editLink = routes.DateLeftUkController.editGet.url,
      changeName = "date you left the UK",
      content = ifComplete(keys.dateLeftUk) {
        s"<p>$yearMonth</p>"
      }
    )
  }

  def dateLeftForces = {
    val yearMonth = form.dateLeftSpecial map { _.toString("MMMM, yyyy") } getOrElse ""

    ConfirmationQuestion(
      title = "Date you cease to be a member of the armed forces",
      editLink = routes.DateLeftArmyController.editGet.url,
      changeName = "date you cease to be a member of the armed forces",
      content = ifComplete(keys.dateLeftSpecial) {
        s"<p>$yearMonth</p>"
      }
    )
  }

  def dateLeftCrown = {
    val yearMonth = form.dateLeftSpecial map { _.toString("MMMM, yyyy") } getOrElse ""

    ConfirmationQuestion(
      title = "Date you cease to be a Crown Servant",
      editLink = routes.DateLeftCrownController.editGet.url,
      changeName = "date you cease to be a Crown Servant",
      content = ifComplete(keys.dateLeftSpecial) {
        s"<p>$yearMonth</p>"
      }
    )
  }

  def dateLeftCouncil = {
    val yearMonth = form.dateLeftSpecial map { _.toString("MMMM, yyyy") } getOrElse ""

    ConfirmationQuestion(
      title = "Date you cease to be a British Council employee?",
      editLink = routes.DateLeftCrownController.editGet.url,
      changeName = "date you cease to be a British Council employee?",
      content = ifComplete(keys.dateLeftSpecial) {
        s"<p>$yearMonth</p>"
      }
    )
  }
}
