package uk.gov.gds.ier.transaction.crown.previousAddress

import controllers.step.crown.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

class PreviousAddressPostcodeStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets,
    val addressService: AddressService)
  extends CrownStep
  with PreviousAddressPostcodeMustache
  with PreviousAddressForms {

  val validation = postcodeStepForm

  val routing = Routes(
    get = PreviousAddressPostcodeController.get,
    post = PreviousAddressPostcodeController.post,
    editGet = PreviousAddressPostcodeController.editGet,
    editPost = PreviousAddressPostcodeController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    val isPreviousAddressNI = currentState.previousAddress.exists(
      _.previousAddress.exists(prevAddr => addressService.isNothernIreland(prevAddr.postcode)))

    if (isPreviousAddressNI) {
      controllers.step.crown.NationalityController.nationalityStep
    } else {
      controllers.step.crown.PreviousAddressSelectController.previousAddressSelectStep
    }
  }

  override val onSuccess = TransformApplication { currentState =>
    val prevAddressCleaned = currentState.previousAddress.map { prev =>
      prev.copy(
        previousAddress = prev.previousAddress.map(_.copy(
          addressLine = None,
          uprn = None,
          manualAddress = None,
          gssCode = None
        ))
      )
    }

    currentState.copy(
      previousAddress = prevAddressCleaned,
      possibleAddresses = None
    )

  } andThen GoToNextIncompleteStep()
}

