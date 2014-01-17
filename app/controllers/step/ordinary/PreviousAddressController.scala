package controllers.step.ordinary

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.ordinary.previousAddress.PreviousAddressStep

object PreviousAddressController extends DelegatingController[PreviousAddressStep] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def previousAddressStep = delegate
}
