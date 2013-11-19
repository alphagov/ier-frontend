package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{ExitController => GuicedController}

object ExitController extends DelegatingController[GuicedController] {
  def scotland = delegate.scotland
  def northernIreland = delegate.northernIreland
}
