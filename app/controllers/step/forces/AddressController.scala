package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.address.{
  AddressFirstStep,
  AddressStep,
  AddressSelectStep,
  AddressManualStep}

object AddressFirstController extends DelegatingController[AddressFirstStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def addressFirstStep = delegate
}

object AddressController extends DelegatingController[AddressStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def addressStep = delegate
}

object AddressSelectController extends DelegatingController[AddressSelectStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def addressSelectStep = delegate
}

object AddressManualController extends DelegatingController[AddressManualStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def addressManualStep = delegate
}
