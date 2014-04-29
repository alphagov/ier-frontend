package uk.gov.gds.ier.transaction.forces.previousAddress

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces

class PreviousAddressPostcodeStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends ForcesStep
  with PreviousAddressPostcodeMustache
  with PreviousAddressForms {

  val validation = postcodeAddressFormForPreviousAddress

  val routes = Routes(
    get = PreviousAddressPostcodeController.get,
    post = PreviousAddressPostcodeController.post,
    editGet = PreviousAddressPostcodeController.editGet,
    editPost = PreviousAddressPostcodeController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    controllers.step.forces.PreviousAddressSelectController.previousAddressSelectStep
  }
}
