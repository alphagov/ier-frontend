package uk.gov.gds.ier.transaction.crown.previousAddress

import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.model.MovedHouseOption
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, CrownStep}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

class PreviousAddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets)
  extends CrownStep
  with PreviousAddressFirstMustache
  with PreviousAddressFirstForms {

  val validation = previousAddressFirstForm

  val routes = Routes(
    get = PreviousAddressFirstController.get,
    post = PreviousAddressFirstController.post,
    editGet = PreviousAddressFirstController.editGet,
    editPost = PreviousAddressFirstController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    currentState.previousAddress.flatMap(_.movedRecently) match {
      case
        Some(MovedHouseOption.YesAndLivingThere) |
        Some(MovedHouseOption.YesAndNotLivingThere) => controllers.step.crown.PreviousAddressPostcodeController.previousPostcodeAddressStep
      case _ => controllers.step.crown.NationalityController.nationalityStep
    }


  }
}

