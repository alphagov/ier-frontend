package uk.gov.gds.ier.controller

import play.api.mvc._
import com.google.inject.Inject
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.mustache.GovukMustache

class RegisterToVoteController @Inject() (val serialiser: JsonSerialiser,
                                          val config: Config,
                                          val encryptionService : EncryptionService)
    extends Controller
    with WithSerialiser
    with WithConfig
    with Logging
    with SessionCleaner
    with WithEncryption
    with GovukMustache {

  def index = Action {
    Redirect(routes.RegisterToVoteController.registerToVote)
  }

  def registerToVote = Action {
    Ok(RegisterToVote.ordinaryStartPage())
  }

  def registerToVoteStart = NewSession requiredFor {
    request =>
      Redirect(step.routes.CountryController.get)
  }

  def registerToVoteOverseas = Action {
    Ok(RegisterToVote.overseasStartPage())
  }

  def registerToVoteOverseasStart = NewSession requiredFor {
    request =>
      Redirect(step.overseas.routes.DateOfBirthController.get)
  }

  def registerToVoteForces = Action {
    Ok(RegisterToVote.forcesStartPage())
  }

  def registerToVoteForcesStart = NewSession requiredFor {
    request =>
      Redirect(step.forces.routes.StatementController.get)
  }

  def registerToVoteCrown = Action {
    Ok(RegisterToVote.crownStartPage())
  }

  def registerToVoteCrownStart = NewSession requiredFor {
    request =>
      Redirect(step.crown.routes.StatementController.get)
  }
}

