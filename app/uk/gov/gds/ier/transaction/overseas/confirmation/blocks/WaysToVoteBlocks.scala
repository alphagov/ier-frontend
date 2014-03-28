package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model.WaysToVoteType._
import controllers.step.overseas.routes
import uk.gov.gds.ier.model.WaysToVoteType

trait WaysToVoteBlocks {
  self: ConfirmationBlock =>

  def waysToVote = {
      val way = form(keys.waysToVote.wayType).value.map(WaysToVoteType.parse(_))
      val prettyWayName = way match {
        case Some(WaysToVoteType.ByPost) => "a postal vote"
        case Some(WaysToVoteType.ByProxy) => "a proxy vote"
        case _ => "an"
      }
      val myEmail = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.getOrElse("")
      val emailMe = form(keys.postalOrProxyVote.deliveryMethod.methodName).value == Some("email")
      val optIn = form(keys.postalOrProxyVote.optIn).value
      val ways = way match {
        case Some(WaysToVoteType.ByPost) => "<p>I want to vote by post</p>"
        case Some(WaysToVoteType.ByProxy) => "<p>I want to vote by proxy (someone else voting for me)</p>"
        case Some(WaysToVoteType.InPerson) => "<p>I want to vote in person, at a polling station</p>"
        case _ => ""
      }
      val postalOrProxyVote = (optIn, emailMe) match {
        case (Some("true"), true) => s"<p>Send an application form to:</p>" +
          s"<p>${myEmail}</p>"
        case (Some("true"), false) => s"<p>Send me an application form in the post</p>"
        case (Some("false"), _) => s"<p>I do not need ${prettyWayName} application form</p>"
        case (_, _) => ""
      }

      Some(ConfirmationQuestion(
        title = "Voting options",
        editLink = routes.WaysToVoteController.editGet.url,
        changeName = "voting",
        content = ifComplete(keys.waysToVote) {
          ways + postalOrProxyVote
        }
      ))
  }  
}

