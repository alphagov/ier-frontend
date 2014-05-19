package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{PostcodeController => GuicedController}

object PostcodeController extends DelegatingController[GuicedController]{

  def lookupAddress(postcode:String) = delegate.lookupAddress(postcode)
}
