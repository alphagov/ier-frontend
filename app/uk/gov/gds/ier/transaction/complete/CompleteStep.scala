package uk.gov.gds.ier.transaction.complete

import play.api.mvc._
import com.google.inject.Inject
import uk.gov.gds.ier.service.PlacesService
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.common.model.{Ero, LocalAuthority}
import uk.gov.gds.ier.session.SessionCleaner
import uk.gov.gds.ier.guice.{WithEncryption, WithConfig}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.{EncryptionKeys, EncryptionService}

class CompleteStep @Inject() (val serialiser: JsonSerialiser,
                              placesService:PlacesService,
                              val config: Config,
                              val encryptionService : EncryptionService,
                              val encryptionKeys : EncryptionKeys)
    extends Controller
    with SessionCleaner
    with WithSerialiser
    with WithConfig
    with WithEncryption
    with CompleteMustache {

  def complete = ClearSession requiredFor {
    implicit request =>
      val authority = request.flash.get("postcode") match {
        case Some("") => None
        case Some(postCode) => placesService.lookupAuthority(postCode)
        case None => None
      }
      val refNum = request.flash.get("refNum")

      Ok(Complete.completePage(authority, refNum))
  }
}
