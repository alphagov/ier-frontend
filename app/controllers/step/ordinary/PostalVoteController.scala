package controllers.step.ordinary

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.ordinary.postalVote.PostalVoteStep

object PostalVoteController extends DelegatingController[PostalVoteStep] {
  
  def get = delegate.get
  def post = delegate.post

  def postalVoteStep = delegate
}
