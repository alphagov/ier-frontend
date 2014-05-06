package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model.LastRegisteredType
import controllers.step.overseas.routes

trait LastRegisteredToVoteBlocks {
  self: ConfirmationBlock =>

  def lastRegistered = {
    import LastRegisteredType._

    val iWas = "I was last registered as"

    val lastRegisteredContent = form.lastRegisteredType match {
      case Some(Overseas) => s"<p>$iWas an overseas voter</p>"
      case Some(Ordinary) => s"<p>$iWas a UK resident</p>"
      case Some(Forces) => s"<p>$iWas a member of the armed forces</p>"
      case Some(Crown) => s"<p>$iWas a Crown servant</p>"
      case Some(Council) => s"<p>$iWas a British council employee</p>"
      case Some(NotRegistered) => "<p>I have never been registered</p>"
      case _ => completeThisStepMessage
    }

    ConfirmationQuestion(
      title = "Last registered to vote",
      editLink = routes.LastRegisteredToVoteController.editGet.url,
      changeName = "last registered to vote",
      content = lastRegisteredContent
    )
  }
}
