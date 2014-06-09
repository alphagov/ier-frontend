package uk.gov.gds.ier.controller

import play.api.mvc.Controller
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.serialiser.{JsonSerialiser, WithSerialiser}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.logging.Logging
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.mustache.ExitPageMustache
import uk.gov.gds.ier.assets.RemoteAssets

class ExitController @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets : RemoteAssets
) extends Controller
  with SessionCleaner
  with ExitPageMustache
  with WithSerialiser
  with Logging
  with WithConfig
  with WithRemoteAssets
  with WithEncryption {

  def scotland = ClearSession requiredFor {
    implicit request =>
      Ok(ExitPages.Scotland())
  }

  def northernIreland = ClearSession requiredFor {
    implicit request =>
      Ok(ExitPages.NorthernIreland())
  }

  def britishIslands = ClearSession requiredFor {
    implicit request =>
      Ok(ExitPages.BritishIslands())
  }

  def under18 = ClearSession requiredFor {
    implicit request =>
      Ok(ExitPages.Under18())
  }

  def tooYoung = ClearSession requiredFor {
    implicit request =>
      Ok(ExitPages.TooYoung())
  }

  def dontKnow = ClearSession requiredFor {
    implicit request =>
      Ok(ExitPages.DontKnow())
  }

  def noFranchise = ClearSession requiredFor {
    implicit request =>
      Ok(ExitPages.NoFranchise())
  }

  def leftUkOver15Years = ClearSession requiredFor {
    request =>
      Ok(ExitPages.LeftUk())
  }

  def tooOldWhenLeftUk = ClearSession requiredFor {
    request =>
      Ok(ExitPages.TooOldWhenLeft())
  }

  def leftSpecialOver15Years = ClearSession requiredFor {
    request =>
      Ok(ExitPages.LeftService())
  }
}
