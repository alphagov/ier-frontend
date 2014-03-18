package controllers.step.crown

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.crown.address.{AddressManualStep, AddressStep, AddressSelectStep}

object AddressController extends DelegatingController[AddressStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
  def lookup = delegate.lookup

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

