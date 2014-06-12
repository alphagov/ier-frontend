package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.feedback.FeedbackPage

object FeedbackController extends DelegatingController[FeedbackPage] {
  def get = delegate.get
  def post = delegate.post
  def thankYou(sourcePath: Option[String]) = delegate.thankYou(sourcePath)

  def feedbackPage = delegate
}
