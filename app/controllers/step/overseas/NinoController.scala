package controllers.step.overseas

import uk.gov.gds.ier.model.InprogressOverseas
import uk.gov.gds.ier.stubs.StubController

object NinoController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/nino"

  def ninoStep = {
    confirmationIf {
      // stub controller always returns that it is not defined, that it contains no data
      _ => false
    }
  }
}
