package controllers.step.ordinary

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.ordinary.previousAddress.{PreviousAddressPostcodeStep, PreviousAddressManualStep, PreviousAddressSelectStep, PreviousAddressFirstStep}

object PreviousAddressFirstController extends DelegatingController[PreviousAddressFirstStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}

object PreviousAddressPostcodeController extends DelegatingController[PreviousAddressPostcodeStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}

object PreviousAddressSelectController extends DelegatingController[PreviousAddressSelectStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}

object PreviousAddressManualController extends DelegatingController[PreviousAddressManualStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}
