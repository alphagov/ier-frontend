package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object LastRegisteredUkAddressController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/last-registered-uk-address"

  def firstTimeStep = confirmationIf {
    // stub controller always returns that it is not defined, that it contains no data
    _ => false
  }
}