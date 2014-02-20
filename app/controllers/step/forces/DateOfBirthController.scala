package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object DateOfBirthController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/date-of-birth"

  def dateOfBirthStep = confirmationIf { application =>
    application.dob.isDefined
  }
}