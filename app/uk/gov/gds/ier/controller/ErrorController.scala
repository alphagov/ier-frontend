package uk.gov.gds.ier.controller

import play.api.mvc._
import uk.gov.gds.ier.config.Config
import com.google.inject.Inject
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.mustache.ErrorPageMustache
import uk.gov.gds.ier.assets.RemoteAssets

class ErrorController @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets
) extends Controller
  with SessionCleaner
  with ErrorPageMustache
  with WithSerialiser
  with WithRemoteAssets
  with Logging
  with WithConfig
  with WithEncryption {

  def ordinaryTimeout = ClearSession requiredFor { request =>
    Ok(ErrorPage.Timeout(
      config.sessionTimeout,
      config.ordinaryStartUrl
    ))
  }

  def forcesTimeout = ClearSession requiredFor { request =>
    Ok(ErrorPage.Timeout(
      config.sessionTimeout,
      config.forcesStartUrl
    ))
  }

  def crownTimeout = ClearSession requiredFor { request =>
    Ok(ErrorPage.Timeout(
      config.sessionTimeout,
      config.crownStartUrl
    ))
  }

  def serverError = ClearSession requiredFor {
    request =>
      InternalServerError(ErrorPage.ServerError())
  }

  def notFound = ClearSession requiredFor {
    request =>
      NotFound(ErrorPage.NotFound(""))
  }
}
