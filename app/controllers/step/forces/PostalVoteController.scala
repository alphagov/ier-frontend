package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object PostalVoteController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/postal-vote"

  def postalVoteStep = confirmationIf { application =>
    application.postalOrProxyVote.isDefined
  }
}