package controllers.step

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.country.{CountryStep => GuicedController}

object CountryController extends DelegatingController[GuicedController] {
  
  def get = delegate.get
  def post = delegate.post
  def editGet = delegate.editGet
  def editPost = delegate.editPost

  def countryStep = delegate
}
