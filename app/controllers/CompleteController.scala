package controllers

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{CompleteController => GuicedController}

object CompleteController extends DelegatingController[GuicedController] {
  
  def complete = delegate.complete
  def fakeComplete = delegate.fakeComplete
}
