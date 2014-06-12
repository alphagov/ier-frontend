package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.feedback.FeedbackPage

object FeedbackController extends DelegatingController[FeedbackPage] {
  def get(sourcePath: Option[String])  = delegate.get(sourcePath)
  def post(sourcePath: Option[String])  = delegate.post(sourcePath)
  def thankYou(sourcePath: Option[String]) = delegate.thankYou(sourcePath)

  def feedbackPage = delegate
}
