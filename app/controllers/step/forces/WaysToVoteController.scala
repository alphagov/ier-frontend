package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.waysToVote.WaysToVoteStep

object WaysToVoteController extends DelegatingController[WaysToVoteStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def waysToVoteStep = delegate
}