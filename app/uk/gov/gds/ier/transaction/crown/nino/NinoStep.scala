package uk.gov.gds.ier.transaction.crown.nino

import uk.gov.gds.ier.transaction.crown.CrownControllers
import controllers.step.crown.routes.{NinoController, JobController}
import controllers.step.crown.ContactAddressController
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.validation._
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class NinoStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets,
    val crown: CrownControllers
) extends CrownStep
  with NinoForms
  with NinoMustache {

  val validation = ninoForm

  val routing = Routes(
    get = NinoController.get,
    post = NinoController.post,
    editGet = NinoController.editGet,
    editPost = NinoController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.ContactAddressStep
  }
}

