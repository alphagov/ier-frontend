package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object OverseasConfirmationController extends StubController[InprogressOverseas] {
  val confirmationStep = overseasConfirmationStep
  val thisStepUrl = "/register-to-vote/overseas/confirmation"
  def overseasConfirmationStep = routeHere()
}
