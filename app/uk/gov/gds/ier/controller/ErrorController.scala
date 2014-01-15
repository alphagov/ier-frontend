package uk.gov.gds.ier.controller

import play.api.mvc._
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.logging.Logging

class ErrorController @Inject ()(val serialiser: JsonSerialiser,
                                 val config: Config,
                                 val encryptionService : EncryptionService,
                                 val encryptionKeys : EncryptionKeys)
  extends Controller
  with SessionCleaner
  with WithSerialiser
  with Logging
  with WithConfig
  with WithEncryption {

  def timeout = ClearSession requiredFor {
    request =>
      Ok(views.html.error.timeoutError(config.sessionTimeout))
  }
}
