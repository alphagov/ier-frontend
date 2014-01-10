package uk.gov.gds.ier.controller

import play.api.mvc._
import com.google.inject.Inject
import views._
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import scala.Some
import org.slf4j.LoggerFactory
import uk.gov.gds.ier.session.{SessionCleaner, SessionHandling}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class RegisterToVoteController @Inject() (val serialiser: JsonSerialiser,
                                          val config: Config,
                                          val encryptionService : EncryptionService,
                                          val encryptionKeys : EncryptionKeys)
    extends Controller
    with WithSerialiser
    with WithConfig
    with Logging
    with SessionCleaner
    with WithEncryption {

  def index = Action {
    logger.info("starting service register to vote")
    Ok(html.start())
  }

  def registerToVote = ClearSession requiredFor {
    request =>
      Redirect(step.routes.CountryController.get)
  }

  def errorRedirect(error:String) = Action {
    Redirect(routes.RegisterToVoteController.error()).flashing("error-type" -> error)
  }

  def error = ClearSession requiredFor {
    implicit request =>
      flash.get("error-type") match {
        case Some("exit-unknown-dob") => Ok(html.errors.exitUnknownDob())
        case Some("exit-nationality") => Ok(html.errors.exitNationality())
        case Some("exit-dob") => Ok(html.errors.exitDob())
        case Some("exit-error") => Ok(html.errors.exitError())
        case _ => Redirect(routes.RegisterToVoteController.index())
      }
  }
}

