package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.passport.PassportCheckStep

object PassportCheckController 
  extends DelegatingController[PassportCheckStep] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def passportCheckStep = delegate
}
