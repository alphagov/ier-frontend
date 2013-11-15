package controllers

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{RegisterToVoteController => GuicedController}

object RegisterToVoteController extends DelegatingController[GuicedController] {
  
  def index = delegate.index
  def error = delegate.error
  def errorRedirect(error:String) = delegate.errorRedirect(error)
  def registerToVote = delegate.registerToVote
}
