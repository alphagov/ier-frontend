package uk.gov.gds.ier.controller

import play.api.mvc._
import com.google.inject.Inject
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.mustache.GovukMustache
import uk.gov.gds.ier.assets.RemoteAssets

class RegisterToVoteController @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets : RemoteAssets)
  extends Controller
  with WithSerialiser
  with WithConfig
  with WithRemoteAssets
  with Logging
  with SessionCleaner
  with WithEncryption
  with GovukMustache {

  def index = Action {
    Redirect(config.ordinaryStartUrl)
  }

  def registerToVote = Action {
    Ok(RegisterToVote.OrdinaryStartPage())
  }

  def registerToVoteStart = NewSession requiredFor {
    request =>
      Redirect(step.routes.CountryController.get.url, request.queryString)
  }

  def registerToVoteOverseasStart = NewSession requiredFor {
    request =>
      Redirect(step.overseas.routes.DateOfBirthController.get.url, request.queryString)
  }

  def registerToVoteForces = Action {
    Ok(RegisterToVote.ForcesStartPage())
  }

  def registerToVoteForcesStart = NewSession requiredFor {
    request =>
      Redirect(step.forces.routes.StatementController.get.url, request.queryString)
  }

  def registerToVoteCrown = Action {
    Ok(RegisterToVote.CrownStartPage())
  }

  def registerToVoteCrownStart = NewSession requiredFor {
    request =>
      Redirect(step.crown.routes.StatementController.get.url, request.queryString)
  }

  def privacy = Action { request =>
    Ok(RegisterToVote.PrivacyPage())
  }

  def cookies = Action { request =>
    Ok(RegisterToVote.CookiePage())
  }
}

