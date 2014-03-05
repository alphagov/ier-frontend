package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.openRegister.OpenRegisterStep

object OpenRegisterController extends DelegatingController[OpenRegisterStep] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def openRegisterStep = delegate
}
