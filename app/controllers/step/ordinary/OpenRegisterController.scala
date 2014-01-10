package controllers.step.ordinary

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.openRegister.{OpenRegisterStep => GuicedController}

object OpenRegisterController extends DelegatingController[GuicedController] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
}
