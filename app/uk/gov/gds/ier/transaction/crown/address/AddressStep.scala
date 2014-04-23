package uk.gov.gds.ier.transaction.crown.address

import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{GoTo, CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import controllers.routes.ExitController

class AddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
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
    currentState.address.map(_.address) match {
      case Some(address) if address.exists(_.postcode.trim.toUpperCase.startsWith("BT")) => GoTo (ExitController.northernIreland)
      case _ => controllers.step.crown.AddressSelectController.addressSelectStep
    }
  }

  override val onSuccess = {
    GoToNextStep()
  }
}
