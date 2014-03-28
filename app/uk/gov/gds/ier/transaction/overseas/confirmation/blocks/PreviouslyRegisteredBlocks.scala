package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model.LastRegisteredType
import controllers.step.overseas.routes

trait PreviouslyRegisteredBlocks {
  self: ConfirmationBlock =>

  def previouslyRegistered = {
    import LastRegisteredType._

    val renewer = form.previouslyRegisteredOverseas
    val prevRegType = form.lastRegisteredType
    val iWas = "I was last registered as"

    val previouslyRegisteredContent = (renewer, prevRegType) match {
      case (Some(true), _) => s"<p>$iWas an overseas voter</p>"
      case (_, Some(Ordinary)) => s"<p>$iWas a UK resident</p>"
      case (_, Some(Forces)) => s"<p>$iWas a member of the armed forces</p>"
      case (_, Some(Crown)) => s"<p>$iWas a Crown servant</p>"
      case (_, Some(Council)) => s"<p>$iWas a British council employee</p>"
      case (_, Some(NotRegistered)) => "<p>I have never been registered</p>"
      case _ => completeThisStepMessage
    }

    val editCall = if(prevRegType.isDefined) {
      routes.LastRegisteredToVoteController.editGet
    } else {
      routes.PreviouslyRegisteredController.editGet
    }

    Some(ConfirmationQuestion(
      title = "Previously Registered",
      editLink = editCall.url,
      changeName = "previously registered",
      content = previouslyRegisteredContent
    ))
  }
}
