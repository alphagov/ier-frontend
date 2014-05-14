package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import controllers.step.overseas.routes.{
  LastUkAddressController,
  DateLeftUkController}
import controllers.step.overseas.LastUkAddressSelectController
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import controllers.routes.ExitController
import uk.gov.gds.ier.step.GoTo
import uk.gov.gds.ier.assets.RemoteAssets

class LastUkAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets
) extends OverseaStep
  with LastUkAddressLookupMustache
  with LastUkAddressForms
  with OverseasFormImplicits
  with WithAddressService {

  val validation = lookupAddressForm

  val routes = Routes(
    get = LastUkAddressController.get,
    post = LastUkAddressController.post,
    editGet = LastUkAddressController.editGet,
    editPost = LastUkAddressController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.lastUkAddress match {
      case Some(partialAddress) => {
        val postcode = partialAddress.postcode.trim.toUpperCase
        if (postcode.isEmpty)
          this
        else if (postcode.startsWith("BT"))
          GoTo (ExitController.northernIreland)
        else if (addressService.isScotland(postcode))
          GoTo (ExitController.scotland)
        else
          LastUkAddressSelectController.lastUkAddressSelectStep
      }
      case None => this
    }
  }
}
