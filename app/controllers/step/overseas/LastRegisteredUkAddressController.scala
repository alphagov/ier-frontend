package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object LastRegisteredUkAddressController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/last-registered-uk-address"

  def firstTimeStep = confirmationIf { application =>
    true // TODO
  }
}