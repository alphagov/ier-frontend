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
import controllers.step.crown.PreviousAddressPostcodeController._

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
    val nextAddressStep = currentState.previousAddress.map(_.previousAddress) match {
      case Some(address) =>
        if (address.exists(_.postcode.isEmpty)) { previousPostcodeAddressStep }
        else if (address.exists(_.manualAddress.isDefined)) { controllers.step.crown.PreviousAddressManualController.previousAddressManualStep }
        else if (address.exists(_.uprn.isDefined)) { controllers.step.crown.PreviousAddressSelectController.previousAddressSelectStep }
        else previousPostcodeAddressStep
      case _ => previousPostcodeAddressStep
    }

    currentState.previousAddress.flatMap(_.movedRecently) match {
      case Some(MovedHouseOption.Yes) => nextAddressStep
      case _ => controllers.step.crown.NationalityController.nationalityStep
    }
  }

  override val onSuccess = TransformApplication { currentApplication =>
    val clearAddress = currentApplication.previousAddress.flatMap(
      _.movedRecently match {
        case Some(MovedHouseOption.NotMoved) => Some(true)
        case _ => Some(false)
      }
    ).getOrElse(false)

    if(clearAddress){
      val clearedAddress = currentApplication.previousAddress.map{ _.copy(previousAddress = None) }

      currentApplication.copy(
        previousAddress = clearedAddress,
        possibleAddresses = None)
    } else {
      currentApplication
    }

  } andThen BranchOn (_.previousAddress.map(_.movedRecently)) {
    case Some(Some(MovedHouseOption.Yes)) => GoToNextStep()
    case _ => GoToNextIncompleteStep()
  }
}

