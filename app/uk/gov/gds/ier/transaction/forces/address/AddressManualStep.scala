package uk.gov.gds.ier.transaction.forces.address

import controllers.step.forces.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{ForcesStepWithNewMustache, ForcesStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import controllers.step.forces.PreviousAddressFirstController
import uk.gov.gds.ier.transaction.forces.InprogressForces


class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends ForcesStepWithNewMustache
  with AddressManualMustache
  with AddressForms {

  val validation = manualAddressForm

  val previousRoute = Some(StatementController.get)

  val routes = Routes(
    get = AddressManualController.get,
    post = AddressManualController.post,
    editGet = AddressManualController.editGet,
    editPost = AddressManualController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    PreviousAddressFirstController.previousAddressFirstStep
  }
}
