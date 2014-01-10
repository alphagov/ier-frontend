package controllers.step.ordinary

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.transaction.confirmation.{ConfirmationStep => GuicedController}

object ConfirmationController extends DelegatingController[GuicedController] {
  def get = delegate.get
  def post = delegate.post
}
