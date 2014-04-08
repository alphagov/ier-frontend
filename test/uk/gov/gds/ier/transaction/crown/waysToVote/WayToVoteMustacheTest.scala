package uk.gov.gds.ier.transaction.crown.waysToVote

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.transaction.crown.InprogressCrown

/**
 * Unit test to test form to Mustache model transformation.
 */
class WayToVoteMustacheTest
  extends FlatSpec
  with Matchers
  with WaysToVoteForms
  with ErrorMessages
  with FormKeys
  with TestHelpers
  with WaysToVoteMustache {

  it should "produce valid empty model when application is empty" in {
    val emptyApplicationForm = waysToVoteForm
    val model = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/crown/ways-to-vote"),
      Some(Call("GET", "/register-to-vote/crown/open-register")),
      InprogressCrown()
    ).data.asInstanceOf[WaysToVoteModel]

    model.question.title should be("How do you want to vote?")
    model.question.postUrl should be("/register-to-vote/crown/ways-to-vote")
    model.question.backUrl should be("/register-to-vote/crown/open-register")

    model.byPost.value should be("by-post")
    model.byProxy.value should be("by-proxy")
    model.inPerson.value should be("in-person")

    model.byPost.attributes should be("")
    model.byProxy.attributes should be("")
    model.inPerson.attributes should be("")
  }

  it should "produce valid model with checked in-person when application has in-person filled" in {
    val emptyApplicationForm = waysToVoteForm.fill(InprogressCrown(
           waysToVote = Some(WaysToVote(WaysToVoteType.InPerson)))
    )
    val model = mustache.data(
      emptyApplicationForm,
      Call("POST", "/register-to-vote/crown/ways-to-vote"),
      Some(Call("GET", "/register-to-vote/crown/open-register")),
      InprogressCrown()
    ).data.asInstanceOf[WaysToVoteModel]

    model.question.title should be("How do you want to vote?")
    model.question.postUrl should be("/register-to-vote/crown/ways-to-vote")
    model.question.backUrl should be("/register-to-vote/crown/open-register")

    model.byPost.value should be("by-post")
    model.byProxy.value should be("by-proxy")
    model.inPerson.value should be("in-person")

    model.byPost.attributes should be("")
    model.byProxy.attributes should be("")
    model.inPerson.attributes should be("checked=\"checked\"")
  }

}
