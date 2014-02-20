package controllers.step.forces

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.forces.statement.StatementStep

object StatementController extends DelegatingController[StatementStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def statementStep = delegate
}