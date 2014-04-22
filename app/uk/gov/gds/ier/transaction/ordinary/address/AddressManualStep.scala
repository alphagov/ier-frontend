package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes.{
  AddressController,
  AddressManualController,
  NinoController}
import controllers.step.ordinary.OtherAddressController
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{GoTo, OrdinaryStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import controllers.routes.ExitController
import uk.gov.gds.ier.service.AddressService

class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OrdinaryStep
  with AddressManualMustache
  with AddressForms {

  val validation = manualAddressForm

  val previousRoute = Some(NinoController.get)

  val routes = Routes(
    get = AddressManualController.get,
    post = AddressManualController.post,
    editGet = AddressManualController.editGet,
    editPost = AddressManualController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
      OtherAddressController.otherAddressStep
  }
}
