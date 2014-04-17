package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes.{
  AddressController,
  NinoController}
import controllers.step.ordinary.AddressSelectController
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, OrdinaryStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import controllers.routes.ExitController

class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OrdinaryStep
  with AddressMustache
  with AddressForms {

  val validation = lookupAddressForm
  val previousRoute = Some(NinoController.get)

  val routes = Routes(
    get = AddressController.get,
    post = AddressController.post,
    editGet = AddressController.editGet,
    editPost = AddressController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    val optAddress = currentState.address 
    
    if (optAddress.exists(_.postcode.toUpperCase.startsWith("BT"))) 
      GoTo (ExitController.northernIreland)
    else {
      val isScotland = optAddress.exists { add =>
        addressService.lookupPartialAddress(add.postcode).head.gssCode.exists(_.startsWith("S"))
      }
      if (isScotland) 
        GoTo (ExitController.scotland)
      else {
        AddressSelectController.addressSelectStep
      }
    }
  }

  override val onSuccess = {
     GoToNextStep()
  }
}
