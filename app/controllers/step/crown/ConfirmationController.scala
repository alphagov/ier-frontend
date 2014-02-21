package controllers.step.crown

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.crown.confirmation.ConfirmationStep

object ConfirmationController extends DelegatingController[ConfirmationStep] {
  def get = delegate.get
  def post = delegate.post

  def confirmationStep = delegate
}
