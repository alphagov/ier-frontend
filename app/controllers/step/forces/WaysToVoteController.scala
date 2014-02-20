package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object WaysToVoteController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/ways-to-vote"

  def waysToVoteStep = confirmationIf { application =>
    application.waysToVote.isDefined
  }
}