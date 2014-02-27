package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.parentsAddress.{ParentsAddressStep, ParentsAddressSelectStep, ParentsAddressManualStep}


object ParentsAddressController extends DelegatingController[ParentsAddressStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
  def lookup = delegate.lookup

  def parentsAddressStep = delegate
}

object ParentsAddressSelectController extends DelegatingController[ParentsAddressSelectStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def parentsAddressSelectStep = delegate
}

object ParentsAddressManualController extends DelegatingController[ParentsAddressManualStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def parentsAddressManualStep = delegate
}

