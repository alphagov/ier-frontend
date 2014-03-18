package controllers.step.crown

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.crown.contactAddress.ContactAddressStep

object ContactAddressController extends DelegatingController[ContactAddressStep] {

  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def contactAddressStep = delegate
}