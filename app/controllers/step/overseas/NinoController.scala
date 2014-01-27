package controllers.step.overseas

import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.stubs.StubController

object NinoController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/nino"

  def ninoStep = {
    confirmationIf {
      application => true // TODO
    }
  }
}
