package controllers.step.overseas

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.overseas.dateOfBirth.DateOfBirthStep

object DateOfBirthController extends DelegatingController[DateOfBirthStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def dateOfBirthStep = delegate
}
