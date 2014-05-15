package uk.gov.gds.ier.transaction.crown.address

import controllers.step.crown.routes._
import com.google.inject.Inject
import uk.gov.gds.ier.serialiser.JsonSerialiser
import uk.gov.gds.ier.config.Config
import uk.gov.gds.ier.security.EncryptionService
import uk.gov.gds.ier.service.AddressService

import uk.gov.gds.ier.step.{Routes, CrownStep}
import uk.gov.gds.ier.transaction.crown.InprogressCrown
import uk.gov.gds.ier.assets.RemoteAssets
import controllers.step.crown.AddressController._

class AddressFirstStep @Inject ()(
    val serialiser: JsonSerialiser,
    val config: Config,
    val encryptionService : EncryptionService,
    val addressService: AddressService,
    val remoteAssets: RemoteAssets)
  extends CrownStep
  with AddressFirstMustache
  with AddressFirstForms {

  val validation = addressFirstForm

  val routes = Routes(
    get = AddressFirstController.get,
    post = AddressFirstController.post,
    editGet = AddressFirstController.editGet,
    editPost = AddressFirstController.editPost
  )

  def nextStep(currentState: InprogressCrown) = {
    currentState.address.map(_.address) match {
      case Some(address) =>
        if (address.exists(_.postcode.isEmpty)) { addressStep }
        else if (address.exists(_.manualAddress.isDefined)) { controllers.step.crown.AddressManualController.addressManualStep }
        else if (address.exists(_.uprn.isDefined)) { controllers.step.crown.AddressSelectController.addressSelectStep }
        else addressStep
      case _ => addressStep
    }
  }

  override val onSuccess = GoToNextStep()
}

