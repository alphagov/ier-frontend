package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.{
  LastUkAddressStep, 
  LastUkAddressSelectStep,
  LastUkAddressManualStep}

object LastUkAddressController extends DelegatingController[LastUkAddressStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def lastUkAddressStep = delegate
}

object LastUkAddressSelectController extends DelegatingController[LastUkAddressSelectStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def lastUkAddressSelectStep = delegate
}

object LastUkAddressManualController extends DelegatingController[LastUkAddressManualStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def lastUkAddressManualStep = delegate
}
