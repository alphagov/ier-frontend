package uk.gov.gds.ier.transaction.overseas.lastUkAddress

import controllers.step.overseas.routes.{
  LastUkAddressController,
  DateLeftUkController}
import controllers.step.overseas.{
  NameController, LastUkAddressSelectController,
  PassportCheckController}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.ApplicationType
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.form.OverseasFormImplicits
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import controllers.routes.ExitController
import uk.gov.gds.ier.step.GoTo

class LastUkAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OverseaStep
  with LastUkAddressLookupMustache
  with LastUkAddressForms
  with OverseasFormImplicits
  with WithAddressService {

  val validation = lookupAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = LastUkAddressController.get,
    post = LastUkAddressController.post,
    editGet = LastUkAddressController.editGet,
    editPost = LastUkAddressController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.lastUkAddress.map(_.postcode) match {
      case Some(postcode) => {
        if (postcode.trim.toUpperCase.startsWith("BT")) 
          GoTo (ExitController.northernIreland)
        else {
          LastUkAddressSelectController.lastUkAddressSelectStep
        } 
      }
      case _ => this 
    }
  }
}
