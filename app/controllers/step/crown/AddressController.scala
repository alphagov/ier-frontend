package controllers.step.crown

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressCrown

object AddressController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/address"

  def lookup = fakeAction
  def editLookup = fakeAction

  def addressStep = confirmationIf { application =>
    application.address.isDefined
  }
}