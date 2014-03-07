package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.applicationFormVote.PostalVoteStep

object PostalVoteController extends DelegatingController[PostalVoteStep] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def postalVoteStep = delegate
}
