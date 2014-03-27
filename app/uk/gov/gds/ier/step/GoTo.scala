package uk.gov.gds.ier.step

import controllers.routes._
import play.api.mvc.Call
import play.api.mvc.Results.Redirect

case class GoTo[T](redirectCall:Call) extends Step[T] {
  def isStepComplete(currentState: T): Boolean = false

  def nextStep(currentState: T): Step[T] = this

  val routes = Routes(
    get = redirectCall,
    post = redirectCall,
    editGet = redirectCall,
    editPost = redirectCall
  )
}
