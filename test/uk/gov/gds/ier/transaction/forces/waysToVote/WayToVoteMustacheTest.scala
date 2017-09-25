package uk.gov.gds.ier.transaction.forces.waysToVote

import uk.gov.gds.ier.test._
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.model.WaysToVote
import uk.gov.gds.ier.transaction.forces.InprogressForces

/**
 * Unit test to test form to Mustache model transformation.
 */
class WayToVoteMustacheTest
  extends MustacheTestSuite
  with WaysToVoteForms
  with WaysToVoteMustache {

    it should "produce valid empty model when application is empty" in {
      val emptyApplicationForm = waysToVoteForm
      val model = mustache.data(
        emptyApplicationForm,
        Call("POST", "/register-to-vote/forces/ways-to-vote"),
        InprogressForces()
      ).asInstanceOf[WaysToVoteModel]

      model.question.title should be("www.gov.uk/register-to-vote - How do you want to vote?")
      model.question.postUrl should be("/register-to-vote/forces/ways-to-vote")

      model.byPost.value should be("by-post")
      model.byProxy.value should be("by-proxy")
      model.inPerson.value should be("in-person")

      model.byPost.attributes should be("")
      model.byProxy.attributes should be("")
      model.inPerson.attributes should be("")
    }

    it should "produce valid model with checked in-person when application has in-person filled" in {
      val emptyApplicationForm = waysToVoteForm.fill(InprogressForces(
        waysToVote = Some(WaysToVote(WaysToVoteType.InPerson)))
      )
      val model = mustache.data(
        emptyApplicationForm,
        Call("POST", "/register-to-vote/forces/ways-to-vote"),
        InprogressForces()
      ).asInstanceOf[WaysToVoteModel]

      model.question.title should be("www.gov.uk/register-to-vote - How do you want to vote?")
      model.question.postUrl should be("/register-to-vote/forces/ways-to-vote")

      model.byPost.value should be("by-post")
      model.byProxy.value should be("by-proxy")
      model.inPerson.value should be("in-person")

      model.byPost.attributes should be("")
      model.byProxy.attributes should be("")
      model.inPerson.attributes should be("checked=\"checked\"")
    }

}
