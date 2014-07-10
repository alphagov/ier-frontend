package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{ErrorController => GuicedController}

object ErrorController extends DelegatingController[GuicedController] {

  def ordinaryTimeout = delegate.ordinaryTimeout
  def forcesTimeout = delegate.forcesTimeout
  def crownTimeout = delegate.crownTimeout
  def serverError = delegate.serverError
  def notFound = delegate.notFound
}
