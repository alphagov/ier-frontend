package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object AddressController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/address"

  def lookup = fakeAction
  def addressStep = confirmationIf { application =>
    application.address.isDefined
  }
}