package controllers.step.crown

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressCrown

object AddressController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/address"
  def lookup = fakeAction

  def addressStep = confirmationIf { application =>
    application.address.isDefined
  }
}

object AddressSelectController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/address/select"

  def addressSelectStep = confirmationIf { application =>
    application.address.isDefined
  }
}

object AddressManualController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/address/manual"

  def addressManualStep = confirmationIf { application =>
    application.address.isDefined
  }
}
