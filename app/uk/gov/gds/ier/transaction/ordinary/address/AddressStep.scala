package uk.gov.gds.ier.transaction.ordinary.address

import controllers.step.ordinary.routes.AddressController
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, OrdinaryStep, Routes}
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary
import controllers.routes.ExitController
import uk.gov.gds.ier.assets.RemoteAssets
import uk.gov.gds.ier.transaction.ordinary.OrdinaryControllers

class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with AddressMustache
  with AddressForms {

  val validation = lookupAddressForm

  val routing = Routes(
    get = AddressController.get,
    post = AddressController.post,
    editGet = AddressController.editGet,
    editPost = AddressController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    val optAddress = currentState.address

    optAddress match {
      case Some(partialAddress) => {
        val postcode = partialAddress.postcode.trim.toUpperCase
        if (postcode.isEmpty)
          this
        else if (postcode.startsWith("BT"))
          GoTo (ExitController.northernIreland)
        else if (addressService.isScotland(postcode))
          GoTo (ExitController.scotland)
        else ordinary.AddressSelectStep
      }
      case None => this
    }
  }

  override val onSuccess = {
     GoToNextStep()
  }
}
