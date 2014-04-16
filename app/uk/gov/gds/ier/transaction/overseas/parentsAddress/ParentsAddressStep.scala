package uk.gov.gds.ier.transaction.overseas.parentsAddress

import controllers.step.overseas.routes._
import controllers.step.overseas.PassportCheckController
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.crown.address.WithAddressService
import controllers.routes.ExitController

class ParentsAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OverseaStep
  with ParentsAddressLookupMustache
  with ParentsAddressForms
  with WithAddressService {

  val validation = parentsAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = ParentsAddressController.get,
    post = ParentsAddressController.lookup,
    editGet = ParentsAddressController.editGet,
    editPost = ParentsAddressController.lookup
  )

  def nextStep(currentState: InprogressOverseas) = {
    PassportCheckController.passportCheckStep
  }

  def lookup = ValidSession requiredFor { implicit request => application =>
    parentsLookupAddressForm.bindFromRequest().fold(
      hasErrors => {
        Ok(mustache(hasErrors, routes.post, previousRoute, application).html)
      },
      success => {
        val optAddress = success.parentsAddress 
        if (optAddress.exists(_.postcode.toUpperCase.startsWith("BT"))) 
          Redirect (ExitController.northernIreland)
        else {
          val mergedApplication = success.merge(application)
          Redirect(
            ParentsAddressSelectController.get
          ) storeInSession mergedApplication
        }
      }
    )
  }
}
