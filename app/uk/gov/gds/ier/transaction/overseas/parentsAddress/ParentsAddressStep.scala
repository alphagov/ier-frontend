package uk.gov.gds.ier.transaction.overseas.parentsAddress

import controllers.step.overseas.routes._
import controllers.step.overseas.{PassportCheckController, ParentsAddressSelectController}
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import controllers.routes.ExitController
import uk.gov.gds.ier.step.GoTo

class ParentsAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OverseaStep
  with ParentsAddressLookupMustache
  with ParentsAddressForms
  with WithAddressService {

  val validation = parentsLookupAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = ParentsAddressController.get,
    post = ParentsAddressController.post,
    editGet = ParentsAddressController.editGet,
    editPost = ParentsAddressController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    currentState.parentsAddress.map(_.postcode) match {
      case Some(postcode) => {
        if (postcode.trim.toUpperCase.startsWith("BT"))
          GoTo (ExitController.northernIreland)
        else {
          ParentsAddressSelectController.parentsAddressSelectStep
        }
      }
      case _ => this
    }
  }
}
