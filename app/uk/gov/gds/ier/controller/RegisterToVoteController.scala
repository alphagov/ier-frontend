package uk.gov.gds.ier.controller

import play.api.mvc._
import com.google.inject.Inject
import views._
import controllers._
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import scala.Some
import org.slf4j.LoggerFactory
import uk.gov.gds.ier.session.SessionHandling
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
    with SessionHandling
    with WithEncryption {

  def index = Action {
    logger.info("starting service register to vote")
    Ok(html.start())
  }

  def registerToVote = NewSession requiredFor {
    request =>
      Redirect(step.routes.CountryController.get)
  }
}

