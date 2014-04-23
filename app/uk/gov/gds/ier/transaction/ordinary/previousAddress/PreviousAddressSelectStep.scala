package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.OrdinaryStep
import controllers.step.ordinary.OpenRegisterController
import uk.gov.gds.ier.model.Addresses
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class PreviousAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OrdinaryStep
  with PreviousAddressSelectMustache
  with PreviousAddressForms {

  val validation = selectStepForm

  val routes = Routes(
    get = PreviousAddressSelectController.get,
    post = PreviousAddressSelectController.post,
    editGet = PreviousAddressSelectController.editGet,
    editPost = PreviousAddressSelectController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    OpenRegisterController.openRegisterStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithLineFilled = currentState.previousAddress.map { prev =>
      prev.copy(
        previousAddress = prev.previousAddress.map(addressService.fillAddressLine)
      )
    }

    currentState.copy(
      previousAddress = addressWithLineFilled,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()
}

