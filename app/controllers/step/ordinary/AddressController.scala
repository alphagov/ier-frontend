package controllers.step.ordinary

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.ordinary.address.AddressStep

object AddressController extends DelegatingController[AddressStep] {
  
  def get = delegate.get
  def post = delegate.post
  def lookup = delegate.lookup

  def addressStep = delegate
}
