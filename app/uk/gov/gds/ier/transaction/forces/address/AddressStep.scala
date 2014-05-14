package uk.gov.gds.ier.transaction.forces.address

import controllers.step.forces.routes._
import controllers.step.forces.AddressSelectController
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, ForcesStep, Routes}
import controllers.routes._
import uk.gov.gds.ier.transaction.forces.InprogressForces
import scala.Some
import uk.gov.gds.ier.assets.RemoteAssets

class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets)
  extends ForcesStep
  with AddressLookupMustache
  with AddressForms {

  val validation = lookupAddressForm

  val routes = Routes(
    get = AddressController.get,
    post = AddressController.post,
    editGet = AddressController.editGet,
    editPost = AddressController.editPost
  )

  def nextStep(currentState: InprogressForces) = {

    if (currentState.address.exists(_.address.exists(_.postcode.startsWith("BT"))))
      GoTo (ExitController.northernIreland)
    else if (currentState.address.exists(_.address.exists(addr => addressService.isScotland(addr.postcode))))
      GoTo (ExitController.scotland)
    else AddressSelectController.addressSelectStep
  }

  override val onSuccess = {
    GoToNextStep()
  }
}
