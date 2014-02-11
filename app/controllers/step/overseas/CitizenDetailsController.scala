package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.passport.CitizenDetailsStep

object CitizenDetailsController 
  extends DelegatingController[CitizenDetailsStep] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def citizenDetailsStep = delegate
}
