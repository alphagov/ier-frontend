package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{PostcodeController => GuicedController}

object PostcodeController extends DelegatingController[GuicedController]{

  def lookup(postcode:String) = delegate.lookup(postcode)
}
