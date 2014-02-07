package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.passport.PassportDetailsStep

object PassportDetailsController 
  extends DelegatingController[PassportDetailsStep] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def passportDetailsStep = delegate
}
