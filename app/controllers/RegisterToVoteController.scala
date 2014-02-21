package controllers

import uk.gov.gds.ier.guice.DelegatingController
import uk.gov.gds.ier.controller.{RegisterToVoteController => GuicedController}

object RegisterToVoteController extends DelegatingController[GuicedController] {

  def redirectToOrdinary = delegate.index
  def registerToVote = delegate.registerToVote
  def registerToVoteStart = delegate.registerToVoteStart
  def registerToVoteOverseas = delegate.registerToVoteOverseas
  def registerToVoteOverseasStart = delegate.registerToVoteOverseasStart
  def registerToVoteForces = delegate.registerToVoteForces
  def registerToVoteForcesStart = delegate.registerToVoteForcesStart
  def registerToVoteCrown = delegate.registerToVoteCrown
  def registerToVoteCrownStart = delegate.registerToVoteCrownStart
}
