package uk.gov.gds.ier.transaction.crown.address

import controllers.step.crown.routes._
import com.google.inject.Inject
import play.api.mvc.Call
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.model.LastAddress
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.step.{CrownStep, Routes}
import uk.gov.gds.ier.validation.ErrorTransformForm
import controllers.step.crown.{PreviousAddressFirstController, NationalityController}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets

class AddressManualStep @Inject() (
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService: EncryptionService,
    val remoteAssets: RemoteAssets)
  extends CrownStep
  with AddressManualMustache
  with AddressForms {

  val validation = manualAddressForm

  val routes = Routes(
    get = AddressManualController.get,
    post = AddressManualController.post,
    editGet = AddressManualController.editGet,
    editPost = AddressManualController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    currentState.address match {
      case Some(LastAddress(Some(hasAddressOption),_))
        if (hasAddressOption.hasAddress) => PreviousAddressFirstController.previousAddressFirstStep
      case _ => {
        currentState.copy(
          previousAddress = None
        )
        NationalityController.nationalityStep
      }
    }
  }
}
