package controllers.step.crown

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressCrown

object OpenRegisterController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/open-register"

  def openRegisterStep = confirmationIf { application =>
    application.openRegisterOptin.isDefined
  }
}