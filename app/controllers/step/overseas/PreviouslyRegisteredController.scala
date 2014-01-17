package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.previouslyRegistered.PreviouslyRegisteredStep

object PreviouslyRegisteredController extends DelegatingController[PreviouslyRegisteredStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}
