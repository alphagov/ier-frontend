package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object OpenRegisterController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/open-register"

  def openRegisterStep = confirmationIf { application =>
    application.openRegisterOptin.isDefined
  }
}