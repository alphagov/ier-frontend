package controllers.step.ordinary

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.ordinary.confirmation.ConfirmationStep

object ConfirmationController extends DelegatingController[ConfirmationStep] {
  def get = delegate.get
  def post = delegate.post

  def confirmationStep = delegate
}
