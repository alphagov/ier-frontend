package controllers

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.complete.CompleteStep

object CompleteController extends DelegatingController[CompleteStep] {

  def complete = delegate.complete
}
