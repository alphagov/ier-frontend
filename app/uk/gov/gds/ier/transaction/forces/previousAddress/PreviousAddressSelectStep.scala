package uk.gov.gds.ier.transaction.forces.previousAddress

import controllers.step.forces.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.service.{AddressService, WithAddressService}
import uk.gov.gds.ier.step.{ForcesStep, Routes}
import controllers.step.forces.NationalityController
import uk.gov.gds.ier.model.Addresses
import play.api.mvc.Call
import uk.gov.gds.ier.model.PossibleAddress
import uk.gov.gds.ier.validation.ErrorTransformForm
import uk.gov.gds.ier.model.PartialPreviousAddress
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.transaction.forces.InprogressForces

class PreviousAddressSelectStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val addressService: AddressService)
  extends ForcesStep
  with PreviousAddressSelectMustache
  with PreviousAddressForms
  with WithAddressService {

  val validation = selectAddressFormForPreviousAddress

  val previousRoute = Some(PreviousAddressPostcodeController.get)

  val routes = Routes(
    get = PreviousAddressSelectController.get,
    post = PreviousAddressSelectController.post,
    editGet = PreviousAddressSelectController.editGet,
    editPost = PreviousAddressSelectController.editPost
  )

  def nextStep(currentState: InprogressForces) = {
    NationalityController.nationalityStep
  }

  override val onSuccess = TransformApplication { currentState =>
    val address = currentState.previousAddress.flatMap(_.previousAddress)
    val addressWithAddressLine = address.map {
      addressService.fillAddressLine(_)
    }

    currentState.copy(
      previousAddress = Some(PartialPreviousAddress(
        movedRecently = Some(MovedHouseOption.MovedFromUk),
        addressWithAddressLine
      )),
      possibleAddresses = None
    )
  } andThen GoToNextIncompleteStep()

}
