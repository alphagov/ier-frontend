package uk.gov.gds.ier.transaction.crown.previousAddress

import uk.gov.gds.ier.transaction.crown.CrownControllers
import controllers.step.crown.routes._
import com.google.inject.{Inject, Singleton}
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.CrownStep
import play.api.mvc.Call
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.ErrorTransformForm
import scala.Some
import controllers.step.crown.NationalityController
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.assets.RemoteAssets

@Singleton
class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val encryptionService: EncryptionService,
    val crown: CrownControllers
) extends CrownStep
  with PreviousAddressManualMustache
  with PreviousAddressForms {

  val validation = manualStepForm

  val routing = Routes(
    get = PreviousAddressManualController.get,
    post = PreviousAddressManualController.post,
    editGet = PreviousAddressManualController.editGet,
    editPost = PreviousAddressManualController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    crown.NationalityStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val addressWithClearedSelectedOne = currentState.previousAddress.map { prev =>
      prev.copy(
        previousAddress = prev.previousAddress.map(
          _.copy(
            uprn = None,
            addressLine = None)))
    }

    currentState.copy(
      previousAddress = addressWithClearedSelectedOne,
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()
}
