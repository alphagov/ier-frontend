package uk.gov.gds.ier.step

import controllers.routes._
import play.api.mvc.Call
import play.api.mvc.Results.Redirect

case class Exit[T](redirectCall:Call) extends NextStep[T] {
  override def goToNext(currentState: T) = {
    Redirect(redirectCall)
  }
}
