package uk.gov.gds.ier.transaction.crown.declaration

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{Routes, CrownStep}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.step.crown.NinoController
import controllers.step.crown.routes.DeclarationPdfController
import uk.gov.gds.ier.service.{DeclarationPdfDownloadService, WithDeclarationPdfDownloadService, PlacesService}

class DeclarationPdfStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val placesService: PlacesService,
    val declarationPdfDownloadService: DeclarationPdfDownloadService)
  extends CrownStep
  with WithPlacesService
  with WithDeclarationPdfDownloadService
  with DeclarationPdfForms
  with DeclarationPdfMustache {

  val validation = declarationPdfForm

  val routes = Routes(
    get = DeclarationPdfController.get,
    post = DeclarationPdfController.post,
    editGet = DeclarationPdfController.editGet,
    editPost = DeclarationPdfController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    NinoController.ninoStep
  }
}

