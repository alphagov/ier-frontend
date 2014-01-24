package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object FirstTimeRegisteredController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/first-time-registered"

  def firstTimeStep = confirmationIf { application =>
    application.firstTimeRegistered.isDefined
  }
}
