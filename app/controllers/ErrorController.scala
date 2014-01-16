package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{ErrorController => GuicedController}

object ErrorController extends DelegatingController[GuicedController] {
  
  def timeout = delegate.timeout
}
