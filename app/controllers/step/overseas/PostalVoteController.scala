package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object PostalVoteController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/postal-vote"

  def postalVoteStep = confirmationIf { application =>
    application.address.isDefined
  }
}
