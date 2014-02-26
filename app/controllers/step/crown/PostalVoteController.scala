package controllers.step.crown

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressCrown

object PostalVoteController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/postal-vote"

  def postalVoteStep = confirmationIf { application =>
    application.postalOrProxyVote.isDefined
  }
}