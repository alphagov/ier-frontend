package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.lastRegisteredToVote.LastRegisteredToVoteStep

object LastRegisteredToVoteController extends DelegatingController[LastRegisteredToVoteStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def lastRegisteredToVoteStep = delegate
}
