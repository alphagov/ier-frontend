package uk.gov.gds.ier.transaction.forces.waysToVote

import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait WaysToVoteMustache extends StepTemplate[InprogressForces] {

  val pageTitle = "How do you want to vote?"

  case class WaysToVoteModel(
    question: Question,
    byPost: Field,
    byProxy: Field,
    inPerson: Field
  )

  val mustache = MustacheTemplate("forces/waysToVote") { (form, post, back) =>
    implicit val progressForm = form

    val data = WaysToVoteModel(
      question = Question(
        postUrl = post.url,
        backUrl = back.map { _.url }.getOrElse(""),
        showBackUrl = back.isDefined,
        number = "12",
        title = pageTitle,
        errorMessages = form.globalErrors.map { _.message }),
      byPost = RadioField(
        key = keys.waysToVote.wayType,
        value = "by-post"),
      byProxy = RadioField(
        key = keys.waysToVote.wayType,
        value = "by-proxy"),
      inPerson = RadioField(
        key = keys.waysToVote.wayType,
        value = "in-person")
    )

    MustacheData(data, pageTitle)
  }
}
