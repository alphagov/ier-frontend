package controllers.step

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.step.address.{AddressController => GuicedController}

object AddressController extends DelegatingController[GuicedController] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost
  def lookup = delegate.lookup
  def editLookup = delegate.editLookup

}
