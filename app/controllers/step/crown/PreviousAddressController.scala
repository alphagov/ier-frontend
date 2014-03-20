package controllers.step.crown

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.crown.previousAddress.{PreviousAddressPostcodeStep, PreviousAddressManualStep, PreviousAddressSelectStep, PreviousAddressFirstStep}

object PreviousAddressFirstController extends DelegatingController[PreviousAddressFirstStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def previousAddressFirstStep = delegate
}

object PreviousAddressPostcodeController extends DelegatingController[PreviousAddressPostcodeStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
  def lookup = delegate.lookup

  def previousPostcodeAddressStep = delegate
}

object PreviousAddressSelectController extends DelegatingController[PreviousAddressSelectStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def previousAddressSelectStep = delegate
}

object PreviousAddressManualController extends DelegatingController[PreviousAddressManualStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def previousAddressManualStep = delegate
}
