package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object RegisteredAddressController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/registered-address"

  def registeredAddressStep = confirmationIf { application =>
    application.registeredAddress.isDefined
  }
}
