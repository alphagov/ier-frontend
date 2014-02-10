package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object LastRegisteredToVoteController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/last-registered-to-vote"

  def lastRegisteredToVoteStep = confirmationIf { application =>
    application.lastRegisteredToVote.isDefined
  }
}
