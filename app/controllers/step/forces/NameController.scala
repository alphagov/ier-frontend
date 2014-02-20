package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object NameController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/name"

  def nameStep = confirmationIf { application =>
    application.name.isDefined
  }
}