package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.feedback.FeedbackThankYouPage

object FeedbackThankYouController extends DelegatingController[FeedbackThankYouPage] {
  def get(sourcePath: String) = delegate.get(sourcePath)
  def post = delegate.post
  def feedbackThankYouPage = delegate
}
