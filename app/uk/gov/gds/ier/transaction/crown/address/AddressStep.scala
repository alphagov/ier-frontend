package uk.gov.gds.ier.transaction.crown.address

import controllers.step.crown.AddressSelectController
import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.routes.ExitController
import uk.gov.gds.ier.assets.RemoteAssets

class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets)
  extends CrownStep
  with AddressLookupMustache
  with AddressForms {

  val validation = lookupAddressForm

  val routes = Routes(
    get = AddressController.get,
    post = AddressController.post,
    editGet = AddressController.editGet,
    editPost = AddressController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    val optAddress = currentState.address.flatMap(_.address)

    optAddress match {
      case Some(partialAddress) => {
        val postcode = partialAddress.postcode.trim.toUpperCase
        if (postcode.isEmpty)
          this
        else if (postcode.startsWith("BT"))
          GoTo (ExitController.northernIreland)
        else if (addressService.isScotland(postcode))
          GoTo (ExitController.scotland)
        else AddressSelectController.addressSelectStep
      }
      case None => this
    }
  }

  override val onSuccess = {
    GoToNextStep()
  }
}
