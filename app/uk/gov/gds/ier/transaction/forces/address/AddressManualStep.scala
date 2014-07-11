package uk.gov.gds.ier.transaction.forces.address

import controllers.step.forces.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import controllers.step.forces.{PreviousAddressFirstController, NationalityController}
import uk.gov.gds.ier.transaction.forces.InprogressForces
import uk.gov.gds.ier.model.{HasAddressOption, LastAddress}
import uk.gov.gds.ier.assets.RemoteAssets


class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets)
  extends ForcesStep
  with AddressManualMustache
  with AddressForms {

  val validation = manualAddressForm

  val routing = Routes(
    get = AddressManualController.get,
    post = AddressManualController.post,
    editGet = AddressManualController.editGet,
    editPost = AddressManualController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    val hasUkAddress = Some(true)

    currentState.address flatMap {
      address => address.hasAddress
    } map {
      hasUkAddress => hasUkAddress.hasAddress
    } match {
      case `hasUkAddress` => PreviousAddressFirstController.previousAddressFirstStep
      case _ => NationalityController.nationalityStep
    }
  }

  def clearAddressAndUprn(currentState: InprogressForces)= {
    val clearedAddress = currentState.address.map {addr =>
      addr.copy (address = addr.address.map(
        _.copy(uprn = None, addressLine = None)))
      }

    currentState.copy(
      address = clearedAddress
    )
  }

  override val onSuccess = TransformApplication (clearAddressAndUprn) andThen GoToNextIncompleteStep()
}
