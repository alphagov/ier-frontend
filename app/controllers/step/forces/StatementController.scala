package controllers.step.forces

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressForces

object StatementController extends StubController[InprogressForces] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/forces/statement"

  def statementStep = confirmationIf { application =>
    application.statement.isDefined
  }
}