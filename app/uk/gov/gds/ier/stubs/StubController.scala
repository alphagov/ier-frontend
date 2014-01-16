package uk.gov.gds.ier.stubs

import play.api.mvc.{Call, Controller, Action}
import uk.gov.gds.ier.step.Exit

trait StubController extends Controller {

  def get = fakeAction
  def post = fakeAction
  def editGet = fakeAction
  def editPost = fakeAction

  def route[T](url:String) = Exit[T](Call("GET", url))

  val fakeAction = Action {
    NotFound("This page has not been implemented")
  }
}
