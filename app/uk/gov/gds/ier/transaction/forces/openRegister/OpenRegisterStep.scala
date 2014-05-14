package uk.gov.gds.ier.transaction.forces.openRegister

import controllers.step.forces.WaysToVoteController
import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

class OpenRegisterStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val remoteAssets: RemoteAssets)
  extends ForcesStep
  with OpenRegisterForms
  with OpenRegisterMustache {

  val validation = openRegisterForm

  val routes = Routes(
    get = OpenRegisterController.get,
    post = OpenRegisterController.post,
    editGet = OpenRegisterController.editGet,
    editPost = OpenRegisterController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    WaysToVoteController.waysToVoteStep
  }
  override def isStepComplete(currentState: InprogressForces) = {
    currentState.openRegisterOptin.isDefined
  }
}
