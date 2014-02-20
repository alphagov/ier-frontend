package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object ProxyVoteController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/proxy-vote"

  def proxyVoteStep = confirmationIf { application =>
    application.postalOrProxyVote.isDefined
  }
}