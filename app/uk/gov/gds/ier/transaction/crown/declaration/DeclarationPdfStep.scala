package uk.gov.gds.ier.transaction.crown.declaration

import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{Routes, CrownStep}
import controllers.step.crown.routes._
import scala.Some
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.step.crown.NinoController
import controllers.step.crown.routes.{DeclarationPdfController, JobController}
import uk.gov.gds.ier.service.PlacesService

class DeclarationPdfStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val placesService: PlacesService)
  extends CrownStep
  with WithPlacesService
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

