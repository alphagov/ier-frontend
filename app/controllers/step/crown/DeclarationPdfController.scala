package controllers.step.crown

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.crown.declaration.DeclarationPdfStep
import play.api.mvc.{ResponseHeader, SimpleResult, Action}
import play.api.libs.iteratee.Enumerator
import java.io.File
import play.api.libs.concurrent.Execution.Implicits._
import controllers.routes.Assets

object DeclarationPdfController extends DelegatingController[DeclarationPdfStep] {
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def declarationPdfStep = delegate
}
