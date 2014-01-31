package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.complete.CompleteStep

object CompleteController extends DelegatingController[CompleteStep] {

  def complete = delegate.complete
  def overseasComplete = delegate.complete
}
