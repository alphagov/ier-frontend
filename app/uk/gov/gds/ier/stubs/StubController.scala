package uk.gov.gds.ier.stubs

import play.api.mvc.{Controller, Action}

trait StubController extends Controller {

  def get = fakeAction
  def post = fakeAction
  def editGet = fakeAction
  def editPost = fakeAction

  val fakeAction = Action {
    NotFound("This page has not been implemented")
  }
}
