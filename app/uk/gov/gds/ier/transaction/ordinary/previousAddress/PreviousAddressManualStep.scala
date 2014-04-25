package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.OrdinaryStep
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import controllers.step.ordinary.OpenRegisterController
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService)
  extends OrdinaryStep
  with PreviousAddressManualMustache
  with PreviousAddressForms {

  val validation = manualStepForm

  val routes = Routes(
    get = PreviousAddressManualController.get,
    post = PreviousAddressManualController.post,
    editGet = PreviousAddressManualController.editGet,
    editPost = PreviousAddressManualController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    OpenRegisterController.openRegisterStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithClearedSelectedOne = currentState.previousAddress.map { prev =>
      prev.copy(
        previousAddress = prev.previousAddress.map(_.copy(
          uprn = None,
          addressLine = None
        )))
    }

    currentState.copy(
      previousAddress = addressWithClearedSelectedOne,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()

}
