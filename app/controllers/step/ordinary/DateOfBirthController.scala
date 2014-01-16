package controllers.step.ordinary

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.ordinary.dateOfBirth.DateOfBirthStep

object DateOfBirthController extends DelegatingController[DateOfBirthStep] {
  
  def get = delegate.get
  def post = delegate.post

  def dateOfBirthStep = delegate
}
