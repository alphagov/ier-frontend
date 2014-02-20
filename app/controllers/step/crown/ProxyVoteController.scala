package controllers.step.crown

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressCrown

object ProxyVoteController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/proxy-vote"

  def proxyVoteStep = confirmationIf { application =>
    application.postalOrProxyVote.isDefined
  }
}