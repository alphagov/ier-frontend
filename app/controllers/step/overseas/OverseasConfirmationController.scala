package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.confirmation.ConfirmationStep

object OverseasConfirmationController extends DelegatingController[ConfirmationStep] {
  def get = delegate.get
  def post = delegate.post

  def overseasConfirmationStep = delegate
}
