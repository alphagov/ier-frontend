package controllers.step.ordinary

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.ordinary.otherAddress.OtherAddressStep

object OtherAddressController extends DelegatingController[OtherAddressStep] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def otherAddressStep = delegate
}
