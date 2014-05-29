package uk.gov.gds.ier.transaction.forces.address

import controllers.step.forces.routes._
import controllers.step.forces.{PreviousAddressFirstController, NationalityController}
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.{
  LastAddress,
  HasAddressOption,
  Addresses,
  PossibleAddress}
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.assets.RemoteAssets

class AddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets)
  extends ForcesStep
  with AddressSelectMustache
  with AddressForms
  with WithAddressService {

  val validation = selectStepForm

  val routes = Routes(
    get = AddressSelectController.get,
    post = AddressSelectController.post,
    editGet = AddressSelectController.editGet,
    editPost = AddressSelectController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    import HasAddressOption._

    currentState.address flatMap {
      address => address.hasAddress
    } match {
      case Some(YesAndLivingThere) => PreviousAddressFirstController.previousAddressFirstStep
      case Some(YesAndNotLivingThere) => PreviousAddressFirstController.previousAddressFirstStep
      case _ => NationalityController.nationalityStep
    }
  }

  def fillInAddressAndCleanManualAddress(currentState: InprogressForces) = {
    val addressWithAddressLine = currentState.address.map { lastUkAddress =>
      lastUkAddress.copy (
        address = lastUkAddress.address.map(addressService.fillAddressLine(_).copy(manualAddress = None))
      )
    }

    currentState.copy(
      address = addressWithAddressLine,
      possibleAddresses = None
    )
  }

  override val onSuccess = TransformApplication(fillInAddressAndCleanManualAddress) andThen GoToNextIncompleteStep()

}
