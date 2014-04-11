package uk.gov.gds.ier.transaction.forces.openRegister

import controllers.step.forces.WaysToVoteController
import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.step.{ForcesStepWithNewMustache, ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class OpenRegisterStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService)
  extends ForcesStepWithNewMustache
  with OpenRegisterForms
  with OpenRegisterMustache {

  val validation = openRegisterForm
  val previousRoute = Some(ContactAddressController.get)

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
