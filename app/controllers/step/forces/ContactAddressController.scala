package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object ContactAddressController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/contact-address"

  def contactAddressStep = confirmationIf { application =>
    application.contactAddress.isDefined
  }
}