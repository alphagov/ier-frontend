package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.rank.RankStep

object RankController extends DelegatingController[RankStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def rankStep = delegate
}