package controllers.step

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.country.{CountryStep => GuicedController}

object CountryController extends DelegatingController[GuicedController] {
  
  def get = delegate.get
  def post = delegate.post

  def countryStep = delegate
}
