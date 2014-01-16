package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{RegisterToVoteController => GuicedController}

object RegisterToVoteController extends DelegatingController[GuicedController] {
  
  def index = delegate.index
  def registerToVote = delegate.registerToVote
}
