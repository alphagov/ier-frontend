package controllers

import play.api._
import play.api.mvc._
import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{RegisterToVoteController => GuicedController}

object RegisterToVoteController extends DelegatingController[GuicedController] {
  
  def index = delegate.index
  def complete = delegate.complete
  def error = delegate.error
  def errorRedirect(error:String) = delegate.errorRedirect(error)
  def submitApplication = delegate.submitApplication
  def registerToVote = delegate.registerToVote
}