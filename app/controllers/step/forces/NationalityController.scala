package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.nationality.NationalityStep

object NationalityController extends DelegatingController[NationalityStep] {

  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def nationalityStep = delegate
}