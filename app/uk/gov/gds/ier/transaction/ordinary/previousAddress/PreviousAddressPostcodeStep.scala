package uk.gov.gds.ier.transaction.ordinary.previousAddress

import controllers.step.ordinary.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import play.api.templates.Html
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{MovedHouseOption}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.step.{OrdinaryStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.transaction.ordinary.InprogressOrdinary

class PreviousAddressPostcodeStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends OrdinaryStep
  with PreviousAddressPostcodeMustache
  with PreviousAddressForms {

  val validation = postcodeStepForm

  val routes = Routes(
    get = PreviousAddressPostcodeController.get,
    post = PreviousAddressPostcodeController.post,
    editGet = PreviousAddressPostcodeController.editGet,
    editPost = PreviousAddressPostcodeController.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    val isPreviousAddressNI = currentState.previousAddress.exists(
      _.previousAddress.exists(prevAddr => addressService.isNothernIreland(prevAddr.postcode)))

    if (isPreviousAddressNI) {
      controllers.step.ordinary.OpenRegisterController.openRegisterStep
    } else {
      controllers.step.ordinary.PreviousAddressSelectController.previousAddressSelectStep
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