package uk.gov.gds.ier.transaction.overseas.confirmation.blocks

import uk.gov.gds.ier.model.WaysToVoteType._
import controllers.step.overseas.routes

trait WaysToVoteBlocks {
  self: ConfirmationBlock =>

  def waysToVote = {
    val way = form(keys.waysToVote.wayType).value.map{ way => parse(way) }

    ConfirmationQuestion(
      title = "How do you want to vote",
      editLink = routes.WaysToVoteController.editGet.url,
      changeName = "way to vote",
      content = ifComplete(keys.waysToVote) {
         way match {
          case Some(ByPost) => "<p>By post</p>"
          case Some(ByProxy) => "<p>By proxy (someone else voting for you)</p>"
          case Some(InPerson) => "<p>In the UK, at a polling station</p>"
          case _ => ""
        }
      }
    )
  }

  def postalOrProxyVote = {
    val contactMe = true
    val dontContactMe = false
    val emailMe = true
    val dontEmailMe = false

    val wayToVote = form(keys.postalOrProxyVote.voteType).value.map{ way => parse(way) }
    val prettyWayName = wayToVote match {
      case Some(ByPost) => "postal vote"
      case Some(ByProxy) => "proxy vote"
      case _ => ""
    }
    val myEmail = form(keys.postalOrProxyVote.deliveryMethod.emailAddress).value.getOrElse("")
    val deliveryMethod = form(keys.postalOrProxyVote.deliveryMethod.methodName).value
    val email = form(keys.postalOrProxyVote.deliveryMethod.methodName).value == Some("email")
    val optIn = form(keys.postalOrProxyVote.optIn).value == Some("true")

    val wayToVoteContent = (optIn, email) match {
      case (`contactMe`, `emailMe`) => {
        s"<p>Please email a ${prettyWayName} application form to:</p><p>$myEmail</p>"
      }
      case (`contactMe`, `dontEmailMe`) => {
        s"<p>Please post me a ${prettyWayName} application form</p>"
      }
      case (_, _) => s"<p>I do not need a ${prettyWayName} application form</p>"
    }

    wayToVote match {
      case Some(ByPost) => ConfirmationQuestion(
        title = "Application form",
        editLink = routes.PostalVoteController.editGet.url,
        changeName = "your postal vote form",
        content = ifComplete(keys.postalOrProxyVote) { wayToVoteContent }
      )
      case Some(ByProxy) => ConfirmationQuestion(
        title = "Application form",
        editLink = routes.ProxyVoteController.editGet.url,
        changeName ="your proxy vote form",
        content = ifComplete(keys.postalOrProxyVote) { wayToVoteContent }
      )
      case _ => ConfirmationQuestion(
        title = "Application form",
        editLink = routes.WaysToVoteController.editGet.url,
        changeName = "your method of voting",
        content = ifComplete(keys.postalOrProxyVote) { wayToVoteContent }
      )
    }
  }
}

