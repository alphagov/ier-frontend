package uk.gov.gds.ier.stubs

import play.api.mvc.{Call, Controller, Action}
import uk.gov.gds.ier.step.{Exit, NextStep}

trait StubController[T] extends Controller {

  def get = fakeAction
  def post = fakeAction
  def editGet = fakeAction
  def editPost = fakeAction

  val confirmationStep:NextStep[T]
  val thisStepUrl:String

  def routeHere() = Exit[T](Call("GET", thisStepUrl))
  def confirmationIf(predicate: T => Boolean) = ConfirmationOrNextStep(predicate)

  val fakeAction = Action {
    NotFound("This page has not been implemented")
  }

  case class ConfirmationOrNextStep(predicate:T => Boolean) extends NextStep[T] {
    override def goToNext(currentState: T) = {
      if (predicate(currentState)) {
        confirmationStep.goToNext(currentState)
      } else {
        Redirect(Call("GET", thisStepUrl))
      }
    }
  }
}
