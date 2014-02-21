package controllers.step.crown

import uk.gov.gds.ier.stubs.StubController
import uk.gov.gds.ier.model.InprogressCrown

object StatementController extends StubController[InprogressCrown] {
  val confirmationStep = ConfirmationController.confirmationStep
  val thisStepUrl = "/register-to-vote/crown/statement"

  def statementStep = confirmationIf { application =>
    application.statement.isDefined
  }
}