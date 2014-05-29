package uk.gov.gds.ier.transaction.complete

import play.api.mvc._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.guice.{WithRemoteAssets, WithEncryption, WithConfig}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.service.apiservice.EroAuthorityDetails
import uk.gov.gds.ier.langs.Language

class CompleteStep @Inject() (
    val serialiser: JsonSerialiser,
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
      implicit val lang = Language.getLang(request)
      val authority = request.flash.get("localAuthority") match {
        case Some("") => None
        case Some(authorityJson) => Some(serialiser.fromJson[EroAuthorityDetails](authorityJson))
        case None => None
      }
      val refNum = request.flash.get("refNum")
      val hasOtherAddress = request.flash.get("hasOtherAddress").map(_.toBoolean).getOrElse(false)
      val backToStartUrl = request.flash.get("backToStartUrl").getOrElse("")

      Ok(Complete.CompletePage(authority, refNum, hasOtherAddress, backToStartUrl))
  }
}
