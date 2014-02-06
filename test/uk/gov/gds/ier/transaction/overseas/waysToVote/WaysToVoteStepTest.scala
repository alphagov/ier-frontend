package uk.gov.gds.ier.transaction.overseas.waysToVote

import org.scalatest.{Matchers, FlatSpec}
import uk.gov.gds.ier.transaction.overseas.waysToVote.WaysToVoteStep
import uk.gov.gds.ier.validation.{FormKeys, ErrorMessages}
import uk.gov.gds.ier.test.TestHelpers
import com.google.inject.Guice
import play.api.test.FakeRequest

/**
 * Test as documentation with focus on binding, validation and isStepComplete behaviour
 * found in every Step on example of WaysToVoteStep.
 * I wrote this test for myself to understand how this thing works, but I found it generally useful.
 * Most of the tested is covered in WaysToVoteControllerTest, this is an alternative approach.
 * Use FakeRequest + data map, not JSON, as it is not used in real requests.
 */
class WaysToVoteStepTests
  extends FlatSpec
  with Matchers
  with ErrorMessages
  with FormKeys
  with TestHelpers {

  val injector = Guice.createInjector()
  val waysToVoteStep = injector.getInstance(classOf[WaysToVoteStep])

  it should "indicate WaysToVote step not complete on empty current state of application" in {
    val currentState = emptyOverseasApplication
    val isComplete = waysToVoteStep.isStepComplete(currentState)
    isComplete should be(false)
  }

  it should "indicate WaysToVote step IS complete on complete current state of application" in {
    val currentState = completeOverseasApplication
    val isComplete = waysToVoteStep.isStepComplete(currentState)
    isComplete should be(true)
  }

  it should "indicate WaysToVote step IS complete even when it is the only filled step" in {
    implicit val request = FakeRequest().withFormUrlEncodedBody(
      "waysToVote.wayType" -> "in-person"
    )
    val currentState = emptyOverseasApplication
    val currentStateAfterBinding = waysToVoteStep.validation.bindFromRequest().fold(
      hasErrors => { fail("internal test error") },
      success => { success.merge(currentState) }
    )
    val isComplete = waysToVoteStep.isStepComplete(currentStateAfterBinding)
    isComplete should be(true)
  }

  it should "error out on attempt to fill way to vote type with incorrect value" in {
    implicit val request = FakeRequest().withFormUrlEncodedBody(
      "waysToVote.wayType" -> "foofoo"
    )
    val currentState = emptyOverseasApplication
    val currentStateAfterBinding = waysToVoteStep.validation.bindFromRequest().fold(
      formWithErrors => {
        formWithErrors.errorsAsText should be("" +
          "waysToVote.wayType -> Unknown type")
        formWithErrors.globalErrorsAsText should be("" +
          "Unknown type")
        currentState
      },
      formWithSuccess => { fail("validation was supposed to find some errors") }
    )
    val isComplete = waysToVoteStep.isStepComplete(currentStateAfterBinding)
    isComplete should be(false)
  }
}
