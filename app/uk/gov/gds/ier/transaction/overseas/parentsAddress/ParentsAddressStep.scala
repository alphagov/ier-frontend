package uk.gov.gds.ier.transaction.overseas.parentsAddress

import controllers.step.overseas.routes._
import controllers.step.overseas.PassportCheckController
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OverseaStepWithNewMustache, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas
import uk.gov.gds.ier.transaction.crown.address.WithAddressService

class ParentsAddressStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OverseaStepWithNewMustache
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
        Ok(template(hasErrors, routes.post, previousRoute))
      },
      success => {
        val mergedApplication = success.merge(application)
        Redirect(
          ParentsAddressSelectController.get
        ) storeInSession mergedApplication
      }
    )
  }
}
