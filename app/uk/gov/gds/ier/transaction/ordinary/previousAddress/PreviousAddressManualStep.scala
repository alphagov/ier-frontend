package uk.gov.gds.ier.transaction.ordinary.previousAddress

import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model._
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.OrdinaryStep
import uk.gov.gds.ier.step.Routes
import uk.gov.gds.ier.validation.{DateValidator, CountryValidator, ErrorTransformForm}
import uk.gov.gds.ier.transaction.ordinary.{OrdinaryControllers, InprogressOrdinary}
import uk.gov.gds.ier.service.AddressService
import uk.gov.gds.ier.assets.RemoteAssets

class PreviousAddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets,
    val ordinary: OrdinaryControllers
) extends OrdinaryStep
  with PreviousAddressManualMustache
  with PreviousAddressForms {

  val validation = manualStepForm

  val routing = Routes(
    get = routes.PreviousAddressManualStep.get,
    post = routes.PreviousAddressManualStep.post,
    editGet = routes.PreviousAddressManualStep.editGet,
    editPost = routes.PreviousAddressManualStep.editPost
  )

  def nextStep(currentState: InprogressOrdinary) = {
    //IF YOUNG SCOTTISH CITIZEN, SKIP THE OPEN REGISTER STEP...
    if(currentState.dob.exists(_.dob.isDefined)) {
      if (CountryValidator.isScotland(currentState.country) && DateValidator.isValidYoungScottishVoter(currentState.dob.get.dob.get)) {
        ordinary.PostalVoteStep
      }
      else {
        ordinary.OpenRegisterStep
      }
    }
    else {
      ordinary.OpenRegisterStep
    }
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
