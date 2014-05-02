package uk.gov.gds.ier.transaction.complete

import play.api.mvc._
import com.google.inject.Inject
import uk.gov.gds.ier.service.PlacesService
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.assets.RemoteAssets

class CompleteStep @Inject() (
    val serialiser: JsonSerialiser,
    placesService:PlacesService,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets
) extends Controller
  with SessionCleaner
  with WithSerialiser
  with WithConfig
  with WithEncryption
  with WithRemoteAssets
  with CompleteMustache {

  def complete = ClearSession requiredFor {
    implicit request =>
      val authority = request.flash.get("postcode") match {
        case Some("") => None
        case Some(postCode) => placesService.lookupAuthority(postCode)
        case None => None
      }
      val refNum = request.flash.get("refNum")
      val hasOtherAddress = request.flash.get("hasOtherAddress").map(_.toBoolean).getOrElse(false)
      val backToStartUrl = request.flash.get("backToStartUrl").getOrElse("")

      Ok(Complete.completePage(authority, refNum, hasOtherAddress, backToStartUrl))
  }
}
