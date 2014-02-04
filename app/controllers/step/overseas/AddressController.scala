package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object AddressController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/address"

  def addressStep = confirmationIf { application =>
    application.address.isDefined
  }
}
