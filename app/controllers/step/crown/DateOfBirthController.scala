package controllers.step.crown

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressCrown

object DateOfBirthController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/date-of-birth"

  def dateOfBirthStep = confirmationIf { application =>
    application.dob.isDefined
  }
}