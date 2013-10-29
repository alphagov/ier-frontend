package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{StatusController => GuicedController}

object StatusController extends DelegatingController[GuicedController] {

  def status = delegate.status
}
