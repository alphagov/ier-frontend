package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{ExitController => GuicedController}

object ExitController extends DelegatingController[GuicedController] {
  def scotland = delegate.scotland
  def northernIreland = delegate.northernIreland
  def under18 = delegate.under18
  def tooYoung = delegate.tooYoung
  def dontKnow = delegate.dontKnow
  def noFranchise = delegate.noFranchise
  def leftUkOver15Years = delegate.leftUkOver15Years
  def tooOldWhenLeftUk = delegate.tooOldWhenLeftUk
}
