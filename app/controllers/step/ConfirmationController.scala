package controllers.step

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.step.confirmation.{ConfirmationController => GuicedController}

object ConfirmationController extends DelegatingController[GuicedController] {
  def get = delegate.get
  def post = delegate.post
}
