package controllers.step.ordinary

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.ordinary.address.{
  AddressStep,
  AddressSelectStep,
  AddressManualStep}

object AddressController extends DelegatingController[AddressStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}

object AddressSelectController extends DelegatingController[AddressSelectStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}

object AddressManualController extends DelegatingController[AddressManualStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}
