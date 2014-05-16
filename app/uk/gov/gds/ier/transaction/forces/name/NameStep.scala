package uk.gov.gds.ier.transaction.forces.name

import controllers.step.forces.NinoController
import controllers.step.forces.routes.{NameController, DateOfBirthController}
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

class NameStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets)
  extends ForcesStep
  with NameForms
  with NameMustache {

  val validation = nameForm

  val routes = Routes(
    get = NameController.get,
    post = NameController.post,
    editGet = NameController.editGet,
    editPost = NameController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    NinoController.ninoStep
  }
}
