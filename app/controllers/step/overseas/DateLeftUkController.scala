package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.dateLeftUk.DateLeftUkStep

object DateLeftUkController extends DelegatingController[DateLeftUkStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def dateLeftUkStep = delegate
}
