package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.confirmation.ConfirmationStep

object ConfirmationController extends DelegatingController[ConfirmationStep] {
  def get = delegate.get
  def post = delegate.post

  def confirmationStep = delegate
}
