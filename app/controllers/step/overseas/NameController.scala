package controllers.step.overseas

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressOverseas

object NameController extends StubController[InprogressOverseas] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/overseas/name"

  def nameStep = confirmationIf { application =>
    application.name.isDefined
  }
}
