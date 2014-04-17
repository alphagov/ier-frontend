package uk.gov.gds.ier.transaction.overseas.parentsAddress

import controllers.step.overseas.routes._
import controllers.step.overseas.PassportCheckController
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.{WithSerialiser, JsonSerialiser}
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{OverseaStep, Routes}
import uk.gov.gds.ier.transaction.overseas.InprogressOverseas

class ParentsAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OverseaStep
  with ParentsAddressSelectMustache
  with ParentsAddressForms
  with WithSerialiser
  with WithAddressService {

  val validation = parentsAddressForm

  val previousRoute = Some(DateLeftUkController.get)

  val routes = Routes(
    get = ParentsAddressSelectController.get,
    post = ParentsAddressSelectController.post,
    editGet = ParentsAddressSelectController.editGet,
    editPost = ParentsAddressSelectController.editPost
  )

  def nextStep(currentState: InprogressOverseas) = {
    PassportCheckController.passportCheckStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithAddressLine = currentState.parentsAddress.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      parentsAddress = addressWithAddressLine,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()
}
