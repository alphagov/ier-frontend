package controllers.step.crown

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.crown.applicationFormVote.ProxyVoteStep

object ProxyVoteController extends DelegatingController[ProxyVoteStep] {

  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def proxyVoteStep = delegate
}
