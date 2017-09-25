package uk.gov.gds.ier.transaction.forces.waysToVote

import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.step.StepTemplate

trait WaysToVoteMustache extends StepTemplate[InprogressForces] {

  val pageTitle = "www.gov.uk/register-to-vote - How do you want to vote?"

  val newQuestion = "How do you want to vote?"

  case class WaysToVoteModel(
    question: Question,
    byPost: Field,
    byProxy: Field,
    inPerson: Field
  ) extends MustacheData

  val mustache = MustacheTemplate("forces/waysToVote") { (form, post) =>
    implicit val progressForm = form

    WaysToVoteModel(
      question = Question(
        postUrl = post.url,
        title = pageTitle,
        newQuestion = newQuestion,
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
  }
}
