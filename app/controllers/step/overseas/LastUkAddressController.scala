package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.lastUkAddress.{
  LastUkAddressStep, 
  LastUkAddressSelectStep}

object LastUkAddressController extends DelegatingController[LastUkAddressStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
  def lookup = delegate.lookup

  def lastUkAddressStep = delegate
}

object LastUkAddressSelectController extends DelegatingController[LastUkAddressSelectStep] {
  def get = delegate.get
  def post = delegate.post

  def lastUkAddressSelectStep = delegate
}
