package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.name.NameStep

object NameController extends DelegatingController[NameStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def nameStep = delegate
}